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

    let lightenDarkenColor = function (col, amt) {
        let usePound = false;
        if (col[0] === "#") {
            col = col.slice(1);
            usePound = true;
        }
        let num = parseInt(col,16);
        let r = (num >> 16) + amt;
        if (r > 255) r = 255;
        else if (r < 0) r = 0;
        let b = ((num >> 8) & 0x00FF) + amt;
        if (b > 255) b = 255;
        else if (b < 0) b = 0;
        let g = (num & 0x0000FF) + amt;
        if (g > 255) g = 255;
        else if (g < 0) g = 0;
        return (usePound?"#":"") + (g | (b << 8) | (r << 16)).toString(16);
    }

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
        this.ws = null;
        this.connect();
    }

    window.socket.prototype.connect = function() {

        if (this.ws!=null) {
            this.ws.close();
            this.status = false;
        }

        this.ws = (typeof (this.protocolHeaders) === "object")
            ? new WebSocket(this.url, this.protocolHeaders)
            : new WebSocket(this.url);

        let that = this;
        this.openCall = null;
        this.ws.onopen = function (ev) {
            if (typeof that.openCall==="function") {
                that.openCall(ev);
            }
        }

        this.closeCall = null;
        this.ws.onclose = function (ev) {
            that.status = false;
            if (typeof that.closeCall==="function") {
                that.closeCall(ev);
            }
        }

        this.errorCall = null;
        this.ws.onerror = function (ev) {
            that.status = false;
            if (typeof that.errorCall==="function") {
                that.errorCall(ev);
            }
        }

        this.messageCall = null;
        this.ws.onmessage = function (ev) {
            if (typeof that.messageCall==="function") {
                that.messageCall(ev);
            }
        }
        this.status = true;
    };
    window.socket.prototype.onOpen = function (openCall) {
        this.openCall = openCall;
    };
    window.socket.prototype.onClose = function (closeCall) {
        this.closeCall = closeCall;
    };
    window.socket.prototype.onError = function (errorCall) {
        this.errorCall = errorCall;
    };
    window.socket.prototype.onMessage = function (messageCall) {
        this.messageCall = messageCall;
    };
    window.socket.prototype.sendString = function (message) {
        if (this.ws!=null && this.status===true) {
            this.ws.send(message);
        }
    };
    window.socket.prototype.sendJson = function (obj) {
        if (this.status===true) {
            this.ws.send(JSON.stringify(obj));
        }
    };
    window.socket.prototype.sendBinary = function (obj) {
        this.ws.send(obj);
    };
    window.socket.prototype.close = function () {
        try {
            this.status = false;
            this.ws.close();
        }
        catch (e) {
            ;
        }
    };

    let game = null;

    window.Game = function() {
        this.container = $("div#agar-container")
        this.canvas = $("canvas#agar-canvas");
        this.context = this.canvas[0].getContext("2d");
        this.socket = null;
        this.pageX = 0;
        this.pageY = 0;
        this.me = null;
        this.players = {};
        this.foods = {};
        this.lastToX = null;
        this.lastToY = null;
        this.resize();
    }

    window.Game.start = function () {
        Game.login(function(token){
            if (game!=null) {
                game.play(token);
            }
        });
        return false;
    }

    window.Game.login = function (onSuccess) {
        let name = $("input[name='name']").val();
        ajax.post("/api/register", JSON.stringify({"name": name}), null, function (obj) {
            if (obj.data != null && obj.data.user_token.length > 1) {
                onSuccess(obj.data.user_token)
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

    window.Game.prototype.play = function (token) {
        let that = this;
        this.socket = new socket("/ws/agar-game", ["User-Token", token]);
        this.socket.onOpen(function () {
            that.players = {};
            that.foods = {};
        });
        this.socket.onClose(function(e){
        });
        this.socket.onMessage(function (msg) {
            if (typeof msg==="undefined" || typeof msg.data==="undefined") {
                return;
            }
            let rec = JSON.parse(msg.data);
            if (typeof rec!=="object") {
                return ;
            }
            if (typeof rec.msg!=="undefined" && rec.msg==="you connected") {
                that.me = rec.data;
                that.moveToX = rec.data.x;
                that.moveToY = rec.data.y;
                that.refreshCanvas();
                that.listenMove();
                that.players = {0:false};
                return;
            }
            if (typeof rec.msg!=="undefined" && rec.msg==="you failed") {
                if (window.confirm("不哭，再来一局 ...")) {
                    window.top.location.href = "/";
                }
                return;
            }
            if (that.me===null) {
                return;
            }
            let oldPlayers = that.players;
            that.players = {0:false};
            // log.info(oldPlayers);
            rec.forEach(function(e) {
                if (e.type==="cell") {
                    if (e.id!==that.me.id) {
                        let _e = oldPlayers[e.id];
                        if (typeof _e==="undefined") {
                            e["ox"]=0; e["oy"]=0; e["tx"]=e.x; e["ty"]=e.y;
                            e.time = new Date().getTime();
                        }
                        else {
                            e["ox"]=_e.x; e["oy"]=_e.y; e["tx"]=e.x; e["ty"]=e.y;
                            e.x = _e.x;
                            e.y = _e.y;
                            e.time = new Date().getTime();
                        }
                        that.players[e.id] = e;
                    }
                    else {
                        that.me.grade = e.grade;
                    }
                }
                else if (e.type==="food") {
                    that.foods[e.id] = e;
                }
                else if (e.type==="remove-food") {
                    delete that.foods[e.id];
                }
            });
            that.players[0] = true;
        });
    };

    window.Game.prototype.refreshCanvas = function() {
        this.canvas.css("background", "#ffffff");
        $("div.form-content").hide();
    };

    window.Game.prototype.drawMap = function() {
        if(this.me==null) {
            return;
        }
        let interval = 50;
        let mapWidth = Math.ceil(this.canvasWidth/interval) * interval;
        let mapHeight = Math.ceil(this.canvasHeight/interval) * interval;
        for (let ii=0; ii<=mapWidth/interval; ii++) {
            let startX = (ii * interval - this.me.x % interval) * this.devicePixelRatio;
            // let startX = Math.floor(ii * interval - this.me.y % interval) - this.devicePixelRatio / 2;
            this.context.moveTo(startX, 0);
            this.context.lineTo(startX, this.canvasHeight);
        }
        for (let ii=0; ii<=mapHeight/interval; ii++) {
            let startY = (ii * interval - this.me.y % interval) * this.devicePixelRatio;
            // let startY = Math.floor(ii * interval - this.me.y % interval) - this.devicePixelRatio / 2;
            this.context.moveTo (0, startY);
            this.context.lineTo(this.canvasWidth, startY);
        }
        this.context.lineWidth = 1;
        this.context.strokeStyle = "#aaa" ;
        this.context.stroke();
    };

    window.Game.prototype.elementOutRange = function(e) {
        let r = Math.sqrt(e.grade / Math.PI);
        let leftX  = this.me.x - (this.canvasWidth / 2) - r;
        let rightX = this.me.x + (this.canvasWidth / 2) + r;
        let downY  = this.me.y - (this.canvasHeight / 2) - r;
        let upY    = this.me.y + (this.canvasHeight / 2) + r;
        return (e.x > rightX || e.x < leftX || e.y > upY || e.y < downY);
    }

    window.Game.prototype.drawElement = function(e, alignCenter) {
        // if (this.elementOutRange(e)) {
        //     return;
        // }
        let x, y;
        if (alignCenter) {
            x = this.canvasWidth / 2;
            y = this.canvasHeight / 2;
        }
        else if (e.type==="cell") {
            let t = new Date().getTime() - e.time;
            if (t>=100) {
                t = 100;
            }
            e.x = (e.tx-e.ox)*t/100+e.ox;
            e.y = (e.ty-e.oy)*t/100+e.oy;
            x = e.x - this.me.x + this.canvasWidth / 2;
            y = e.y - this.me.y + this.canvasHeight / 2;
        }
        else if (e.type==="food") {
            if (this.elementOutRange(e)) {
                return;
            }
            x = e.x - this.me.x + this.canvasWidth / 2;
            y = e.y - this.me.y + this.canvasHeight / 2;
        }
        else {
            return;
        }

        this.context.beginPath();
        this.context.fillStyle = "#" + e.color;
        this.context.moveTo(x, y);
        this.context.arc(x, y, Math.sqrt(e.grade/Math.PI),0,Math.PI*2, false);//x,y坐标,半径,圆周率
        this.context.closePath();
        this.context.fill();

        if (e.type==="food") {
            this.context.beginPath();
            this.context.fillStyle = lightenDarkenColor("#" + e.color, +40);
            this.context.arc(x, y, Math.sqrt(e.grade / Math.PI) * 0.70, 0, Math.PI * 2, false);//
            this.context.closePath();
            this.context.fill();
        }
        if (e.name!=null) {
            let textStyle = "#FFFFFF";
            let strokeStyle = "#444444";
            this.context.font = Math.floor(Math.sqrt(e.grade / Math.PI)*0.68) + "px bold 宋体";
            this.context.fillStyle = textStyle;
            this.context.textAlign = "center";
            this.context.textBaseline = "middle";
            this.context.strokeStyle = strokeStyle;
            this.context.strokeText(e.name, x, y);
            this.context.fillStyle = textStyle;
            this.context.fillText(e.name, x, y);
        }
    };

    window.Game.prototype.runTimer = function() {
        if (this.me==null) {
            let that = this;
            requestAnimationFrame(function(){
                that.runTimer();
            });
            return;
        }
        this.context.canvas.width = this.context.canvas.width;
        // this.context.clearRect(0,0, this.canvasWidth, this.canvasHeight);
        this.drawMap()
        let that = this;
        if (this.players[0]===true) {
            $.each(this.foods, function (userId, food) {
                that.drawElement(food, false);
            });
            $.each(this.players, function (userId, player) {
                that.drawElement(player, false);
            });
        }
        this.drawElement(this.me, true);
        $("span#my-x").html(Math.ceil(this.me.x));
        $("span#my-y").html(Math.ceil(this.me.y));
        requestAnimationFrame(function(){
           that.runTimer();
        });
    };

    window.Game.prototype.listenMove = function() {
        let that = this;
        this.canvas.on("mousemove", function(ev) {
            that.pageX = ev.pageX;
            that.pageY = ev.pageY;
        });
        this.loopMoveData();
        setInterval(function(){
            that.socket.sendJson({x: that.me.tx, y: that.me.ty});
        }, 100);
    };

    // let lastTime = 0;
    window.Game.prototype.loopMoveData = function() {
        if (this.me!=null) {
            let moveXSpeed = (this.pageX - this.width/2) / 300;
            let moveYSpeed = (this.pageY - this.height/2) / 300;
            let maxXSpeed = moveXSpeed>=0 ? 3 : -3;
            let maxYSpeed = moveYSpeed>=0 ? 3 : -3;
            let moveToX = Math.abs(moveXSpeed)>3 ? this.me.x + maxXSpeed : this.me.x + moveXSpeed;
            let moveToY = Math.abs(moveYSpeed)>3 ? this.me.y + maxYSpeed : this.me.y + moveYSpeed;
            let r = Math.sqrt(this.me.grade/Math.PI);
            this.me.x = moveToX < r ? r : (moveToX > 5000-r ? 5000-r : moveToX);
            this.me.y = moveToY < r ? r : (moveToY > 5000-r ? 5000-r : moveToY);

            let moveToTX = this.me.x + maxXSpeed * 2;
            let moveToTY = this.me.y + maxYSpeed * 2;
            this.me["tx"] = moveToTX < r ? r : (moveToTX > 5000-r ? 5000-r : moveToTX);
            this.me["ty"] = moveToTY < r ? r : (moveToTY > 5000-r ? 5000-r : moveToTY);
        }
        let that = this;
        setTimeout(function(){
            that.loopMoveData();
        }, 10);
    };

    $(document).ready(function(){
        game = new Game();
        $(window).bind('resize', function (){
            game.resize();
        });
        game.runTimer();
    });

})(jQuery);

