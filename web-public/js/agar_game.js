(function ($) {

    let log = {
        info: function () {
            if (window.console && window.console.log && arguments.length >= 1) {
                window.console.log("%c >> Count : " + arguments.length + " ", "color:white;background:blue;");
                for (let ii = 0; ii < arguments.length; ii++) {
                    window.console.log(arguments[ii]);
                }
            }
        },
        alert: function () {
            if (window.alert && arguments.length >= 1) {
                let s = ">> Count : " + arguments.length;
                for (let ii = 0; ii < arguments.length; ii++) {
                    s = s + "\n" + arguments[ii];
                }
                window.alert(s);
            }
        }
    };

    let ajax = {
        ajaxRequest: function (url, method, data, headers, onSuccess, onError, onComplete) {
            // set headers
            if ($.type(headers) !== "object" || $.isEmptyObject(headers)) {
                headers = {};
            }
            headers['Ajax-Request'] = "jQuery.KTAnchor";
            headers['Content-Type'] = "application/json; charset=UTF-8"
            let contentType = false;
            if (method === "POST") {
                contentType = (data instanceof FormData) ? false : "application/x-www-form-urlencoded; charset=UTF-8";
            }
            $.ajax({
                "url": url,
                "type": method,
                "data": data,
                "contentType": contentType,
                "processData": false,
                "headers": headers,
                "success": function (responseText) {
                    if ($.isFunction(onSuccess)) onSuccess(responseText);
                },
                "error": function (XMLHttpRequest) {
                    if ($.isFunction(onError)) onError(XMLHttpRequest);
                },
                "complete": function (XMLHttpRequest) {
                    if ($.isFunction(onComplete)) onComplete(XMLHttpRequest);
                }
            });
        },
        // get request
        get: function (url, headers, onSuccess, onError) {
            ajax.ajaxRequest(url, "GET", null, headers, function (responseText) {
                ajax.onResponse(responseText, onSuccess, onError);
            });
        },
        // post request
        post: function (url, data, headers, onSuccess, onError) {
            ajax.ajaxRequest(url, "POST", data, headers, function (responseText) {
                ajax.onResponse(responseText, onSuccess, onError);
            });
        },
        // on response
        onResponse: function (obj, onSuccess, onError) {
            if (typeof obj === "string") {
                obj = JSON.parse(obj)
            }
            if (obj.code === 0) {
                if ($.isFunction(onSuccess)) onSuccess(obj);
            } else {
                if ($.isFunction(onError)) onError(obj);
            }
        }
    };

    window.socket = function (uri, protocolHeaders) {
        let protocol = /^https/.test(window.location.protocol) ? "wss\:\/\/" : "ws\:\/\/";
        this.url = /^ws/.test(uri) ? uri : protocol + window.location.host + uri;
        this.protocolHeaders = protocolHeaders;
        this.ws = (typeof (this.protocolHeaders) === "object")
            ? new WebSocket(this.url, this.protocolHeaders)
            : new WebSocket(this.url);
    };
    window.socket.prototype.reconnect = function () {
        this.ws = (typeof (this.protocolHeaders) === "object")
            ? new WebSocket(this.url, this.protocolHeaders)
            : new WebSocket(this.url);
    };
    window.socket.prototype.onOpen = function (callOpen) {
        if (typeof callOpen === "function") {
            this.ws.onopen = callOpen;
        }
        return this;
    };
    window.socket.prototype.onClose = function (callClose) {
        if (typeof callClose === "function") {
            this.ws.onclose = callClose;
        }
        return this;
    };
    window.socket.prototype.onError = function (callError) {
        if (typeof callError === "function") {
            this.ws.onerror = callError;
        }
        return this;
    };
    window.socket.prototype.onMessage = function (callMessage) {
        if (typeof callMessage === "function") {
            this.ws.onmessage = callMessage;
        }
        return this;
    };
    window.socket.prototype.sendString = function (message) {
        this.ws.send(message);
    };
    window.socket.prototype.sendJson = function (obj) {
        this.ws.send(JSON.stringify(obj));
    };
    window.socket.prototype.sendBinary = function (obj) {
        this.ws.send(JSON.stringify(obj));
    };
    window.socket.prototype.close = function () {
        try {
            this.ws.close();
        } catch (e) {
        }
    };

    let game = null;

    window.Game = function() {
        this.container = $("div#agar-container")
        this.canvas = $("canvas#agar-canvas");
        this.context = this.canvas[0].getContext("2d");
        this.socket = null;
        this.moveToX = 0;
        this.moveToY = 0;
        this.me = null;
        this.players = {};
        this.resize();
    }

    window.Game.start = function () {
        Game.login(function(token, user){
            if (game!=null) {
                game.play(token, user);
            }
        });
        return false;
    }

    window.Game.login = function (onSuccess) {
        let name = $("input[name='name']").val();
        let myInfoRequest = function(token) {
            let headers = {"User-Token": token};
            ajax.get("/api/me", headers, function (obj) {
                if (obj && obj.code === 0) {
                    onSuccess(token, obj.data);
                }
            });
        }
        ajax.post("/api/login", JSON.stringify({"name": name}), null, function (obj) {
            if (obj.data != null && obj.data.user_token.length > 1) {
                myInfoRequest(obj.data.user_token, onSuccess)
            }
        });
    }

    window.Game.prototype.resize = function () {
        this.devicePixelRatio = window.devicePixelRatio || 1;
        this.width = $(window).width();
        this.height = $(window).height();
        this.canvasWidth = this.width * this.devicePixelRatio;
        this.canvasHeight = this.height * this.devicePixelRatio;
        this.container.css({"width": this.width, "height": this.height});
        this.canvas.css({"width": this.width, "height": this.height});
        this.canvas.attr({"width": this.canvasWidth, "height": this.canvasHeight});
    }

    window.Game.prototype.play = function (token, user) {
        let that = this;
        this.connect(token, user, function(){
            that.me = user;
            that.moveToX = user.x;
            that.moveToY = user.y;
            that.refreshCanvas();
            that.listenMove();
        }, function (user) {
            if (user.id!==that.me.id) {
                that.players[user.id] = user;
            }
            else {
                that.me = user;
            }
        });
    };

    window.Game.prototype.connect = function(token, user, onOpen, onMessage) {
        this.socket = new socket("/ws/agar-io", ["User-Token", token]);
        this.socket.onOpen(function () {
            onOpen();
        });
        this.socket.onMessage(function (msg) {
            let receiveUsers = msg.data.split(/\n/);
            receiveUsers.forEach(function (receiveUser) {
                receiveUser = receiveUser.split(" ");
                if (receiveUser.length >= 6) {
                    let user = {
                        id: receiveUser[0],
                        name: receiveUser[1],
                        color: receiveUser[2],
                        action: receiveUser[3],
                        gradle: 1 * receiveUser[4],
                        x: 1 * receiveUser[5],
                        y: 1 * receiveUser[6],
                    };
                    onMessage(user);
                }
            });
        });
    }

    window.Game.prototype.refreshCanvas = function() {
        this.canvas.css("background", "#ffffff");
        $("div.form-content").remove();
    };

    window.Game.prototype.runTimer = function() {
        let that = this;
        if (this.me!=null && (this.moveToX !== this.me.x || that.moveToY !== this.me.y)) {
            this.socket.sendString(this.moveToX + " " + this.moveToY);
        }
        setTimeout(function(e){
            that.runTimer();
        },10);
    };

    window.Game.prototype.listenMove = function() {
        let that = this;
        this.canvas.on("mousemove", function(ev) {
            that.moveToX = that.me.x + Math.floor((ev.pageX - that.width/2) / 100);
            that.moveToY = that.me.y + Math.floor((ev.pageY - that.height/2) / 100);
        });
    };

    $(document).ready(function(){
        game = new Game();
        $(window).bind('resize', function (){
            game.resize();
        });
        game.runTimer();
    });

})(jQuery);

