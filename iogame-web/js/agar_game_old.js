//
// (function($){
//
//     let agar = null;
//
//     $.extend({
//         log : {
//             info : function() {
//                 if (window.console && window.console.log && arguments.length >= 1) {
//                     window.console.log("%c >> Count : " + arguments.length + " ", "color:white;background:blue;");
//                     for (let ii = 0; ii < arguments.length; ii++) {
//                         window.console.log(arguments[ii]);
//                     }
//                 }
//             },
//             alert : function() {
//                 if (window.alert && arguments.length >= 1) {
//                     let s = ">> Count : " + arguments.length;
//                     for (let ii = 0; ii < arguments.length; ii++) {
//                         s = s + "\n" +  arguments[ii];
//                     }
//                     window.alert(s);
//                 }
//             }
//         },
//         Ajax: {
//             ajaxRequest: function (url, method, data, headers, onSuccess, onError, onComplete) {
//                 // set headers
//                 if ($.type(headers) !== "object" || $.isEmptyObject(headers)) {
//                     headers = {};
//                 }
//                 headers['Ajax-Request'] = "jQuery.KTAnchor";
//                 headers['Content-Type'] = "application/json; charset=UTF-8"
//                 let contentType = false;
//                 if (method === "POST") {
//                     contentType = (data instanceof FormData) ? false : "application/x-www-form-urlencoded; charset=UTF-8";
//                 }
//                 $.ajax({
//                     "url": url,
//                     "type": method,
//                     "data": data,
//                     "contentType": contentType,
//                     "processData": false,
//                     "headers": headers,
//                     "success": function (responseText) {
//                         if ($.isFunction(onSuccess)) onSuccess(responseText);
//                     },
//                     "error": function (XMLHttpRequest) {
//                         if ($.isFunction(onError)) onError(XMLHttpRequest);
//                     },
//                     "complete": function (XMLHttpRequest) {
//                         if ($.isFunction(onComplete)) onComplete(XMLHttpRequest);
//                     }
//                 });
//             },
//             // get request
//             get: function (url, headers, onSuccess, onError) {
//                 $.Ajax.ajaxRequest(url, "GET", null, headers, function (responseText) {
//                     $.Ajax.onResponse(responseText, onSuccess, onError);
//                 });
//             },
//             // post request
//             post: function (url, data, headers, onSuccess, onError) {
//                 $.Ajax.ajaxRequest(url, "POST", data, headers, function (responseText) {
//                     $.Ajax.onResponse(responseText, onSuccess, onError);
//                 });
//             },
//             // on response
//             onResponse : function(obj, onSuccess, onError) {
//                 if (typeof obj === "string") {
//                     obj = JSON.parse(obj)
//                 }
//                 if (obj.code===0) {
//                     if ($.isFunction(onSuccess)) onSuccess(obj);
//                 }
//                 else {
//                     if ($.isFunction(onError)) onError(obj);
//                 }
//             }
//         },
//
//         WS : function(uri, protocolHeaders) {
//             let protocol = /^https/.test(window.location.protocol) ? "wss\:\/\/" : "ws\:\/\/";
//             this.url = /^ws/.test(uri) ? uri : protocol + window.location.host + uri;
//             this.protocolHeaders = protocolHeaders;
//             this.ws = (typeof(this.protocolHeaders)==="object")
//                 ? new WebSocket(this.url, this.protocolHeaders)
//                 : new WebSocket(this.url);
//             this.reconnect = function() {
//                 this.ws = (typeof(this.protocolHeaders)==="object")
//                     ? new WebSocket(this.url, this.protocolHeaders)
//                     : new WebSocket(this.url);
//             };
//             this.onConnected = function(callOpen) {
//                 if (typeof callOpen==="function") {
//                     this.ws.onopen = callOpen;
//                 }
//                 return this;
//             };
//             this.onClose = function(callClose) {
//                 if (typeof callClose==="function") {
//                     this.ws.onclose = callClose;
//                 }
//                 return this;
//             };
//             this.onError = function(callError) {
//                 if (typeof callError==="function") {
//                     this.ws.onerror = callError;
//                 }
//                 return this;
//             };
//             this.onMessage = function(callMessage) {
//                 if (typeof callMessage==="function") {
//                     this.ws.onmessage = callMessage;
//                 }
//                 return this;
//             };
//             this.send = function(message) {
//                 this.ws.send(message);
//             };
//             this.close = function() {
//                 try {
//                     this.ws.close();
//                 }
//                 catch(e) {
//                 }
//             };
//             return this;
//         },
//
//         User : {
//             login : function() {
//                 let elt = $("input[name='name']");
//                 if (elt.length===1 && elt[0].value.length>1) {
//                     $.Ajax.post("/api/login", JSON.stringify({"name": elt[0].value}), null, function (obj) {
//                         if (obj.data!=null && obj.data.user_token.length>1) {
//                             $.User.me(obj.data.user_token);
//                         }
//                     });
//                 }
//                 return false;
//             },
//             me : function(userToken) {
//                 let headers = {"User-Token":userToken};
//                 $.Ajax.get("/api/me", headers,function (obj) {
//                     if (obj && obj.code===0) {
//                         agar.play(userToken, obj.data);
//                     }
//                 });
//             }
//         }
//     });
//
//     let Map = function() {
//         this.map = agar.context;
//         this.interval = 50;
//         this.x = 0;
//         this.y = 0;
//         this.targetX = 0;
//         this.targetY = 0;
//         this.renderMap(this.x, this.y);
//     };
//
//     Map.prototype.recordMove = function(x, y) {
//         this.targetX = x;
//         this.targetY = y;
//     };
//
//     Map.prototype.renderMap = function(x, y) {
//         // 画竖线
//         let mapWidth = Math.floor(agar.canvasWidth/this.interval) * this.interval;
//         for (let ii=0; ii<mapWidth/this.interval; ii++) {
//             let startX = ii * this.interval * agar.devicePixelRatio - x;
//             this.map.moveTo(startX, 0);
//             this.map.lineTo(startX, agar.canvasHeight);
//         }
//         // 画横线
//         let mapHeight = Math.floor(agar.canvasHeight/this.interval) * this.interval;
//         for (let ii=0; ii<mapHeight/this.interval; ii++) {
//             let startY = ii * this.interval * agar.devicePixelRatio - y;
//             this.map.moveTo (0, startY);
//             this.map.lineTo(agar.canvasWidth, startY);
//         }
//         this.map.lineWidth = 1;
//         this.map.strokeStyle = "#aaa" ;
//         this.map.stroke();
//     };
//
//     Map.prototype.move = function() {
//         this.x = this.x - (agar.width/2 - this.targetX)/100;
//         this.y = this.y - (agar.height/2 - this.targetY)/100;
//         this.renderMap(this.x , this.y);
//     };
//
//     let Player = function(x, y, user) {
//         this.user = (typeof user === "object") ? user : {};
//         this.radio = 30;
//         this.fillStyle = '#'+ user.color;
//         this.textStyle = "#FFFFFF";
//         this.strokeStyle = "#444444";
//         this.targetX = x * agar.devicePixelRatio;
//         this.targetY = y * agar.devicePixelRatio;
//         this.cell = agar.context;
//     };
//
//     Player.prototype.moveTarget = function(x, y) {
//         this.targetX = x * agar.devicePixelRatio;
//         this.targetY = y * agar.devicePixelRatio;
//     };
//
//     Player.prototype.renderCell = function() {
//         this.user.x = this.targetX;
//         this.user.y = this.targetY;
//         this.cell.beginPath();
//         this.cell.fillStyle = this.fillStyle;
//         this.cell.moveTo(this.targetX, this.targetY);
//         this.cell.arc(this.targetX, this.targetY, this.radio,0,Math.PI*2,20);//x,y坐标,半径,圆周率
//         this.cell.closePath();
//         this.cell.fill();
//
//         if (this.user.name!=null) {
//             this.cell.font = "21px bold 黑体";
//             this.cell.fillStyle = this.textStyle;
//             this.cell.textAlign = "center";
//             this.cell.textBaseline = "middle";
//             this.cell.strokeStyle = this.strokeStyle;
//             this.cell.strokeText(this.user.name,this.user.x, this.user.y);
//             this.cell.fillStyle = this.textStyle;
//             this.cell.fillText(this.user.name,this.user.x, this.user.y);
//         }
//     };
//
//     Player.prototype.move = function() {
//         // this.cell.clearRect(0,0, agar.canvasWidth, agar.canvasHeight);
//         this.renderCell();
//     };
//
//     let Agar = function() {
//         this.container = $("div#agar-container")
//         this.canvas = $("canvas#agar-canvas");
//         this.context = this.canvas[0].getContext("2d");
//         this.socket = null;
//         this.me = null;
//         this.players = {};
//         this.initScreen();
//     };
//
//     Agar.prototype.initScreen = function() {
//         this.resize();
//     };
//
//     Agar.prototype.resize = function() {
//         this.devicePixelRatio = window.devicePixelRatio || 1;
//         this.width = $(window).width();
//         this.height = $(window).height();
//         this.canvasWidth = this.width * this.devicePixelRatio;
//         this.canvasHeight = this.height * this.devicePixelRatio;
//         this.container.css({"width": this.width, "height": this.height});
//         this.canvas.css({"width": this.width, "height": this.height});
//         this.canvas.attr({"width": this.canvasWidth, "height": this.canvasHeight});
//     };
//
//     Agar.prototype.play = function(token, user) {
//         let that = this;
//         this.connect(token, user, function(){
//             that.me = user;
//             that.initCanvas();
//             that.initMap();
//             that.createMe(user.x, user.y, user);
//             that.listenMouse();
//             that.runTimer()
//         });
//     };
//
//     Agar.prototype.connect = function(token, user, onSuccess) {
//         let that = this;
//         this.socket = $.WS("/ws/agar-io", ["User-Token", token]);
//         this.socket.onConnected(function () {
//             onSuccess();
//         });
//         this.socket.onMessage(function (msg) {
//             let receiveUsers = msg.data.split(/\n/);
//             receiveUsers.forEach(function(receiveUser){
//                 receiveUser = receiveUser.split(" ");
//                 if (receiveUser.length>=6) {
//                     let user = {
//                         id: receiveUser[0],
//                         name: receiveUser[1],
//                         color: receiveUser[2],
//                         action: receiveUser[3],
//                         gradle: receiveUser[4],
//                         x: receiveUser[5],
//                         y: receiveUser[6],
//                     };
//                     if (user.id!==that.me.user.id) {
//                         that.players[receiveUser[0]] = new Player(user.x, user.y, user);
//                     }
//                     else {
//                         that.me = new Player(user.x, user.y, user);
//                     }
//                 }
//             });
//         });
//     }
//
//     Agar.prototype.initCanvas = function() {
//         this.canvas.css("background", "#ffffff");
//         $("div.form-content").remove();
//     };
//
//     Agar.prototype.initMap = function() {
//         this.map = new Map(this.me);
//         // this.allCell.push(this.map);
//     };
//
//     Agar.prototype.createMe = function(x, y, user) {
//         this.me = new Player(x, y, user);
//     };
//
//     Agar.prototype.listenMouse = function() {
//         let that = this;
//         this.canvas.on("mousemove", function(ev) {
//             let targetX = that.me.user.x + Math.floor((ev.clientX - that.width/2) / 1000);
//             let targetY = that.me.user.y + Math.floor((ev.clientY - that.height/2) / 1000);
//             targetX = (targetX>500) ? 500 : targetX;
//             targetX = (targetX<0) ? 0 : targetX;
//             targetY = (targetY>500) ? 500 : targetY;
//             targetY = (targetY<0) ? 0 : targetY;
//             $.log.info(that.me.user.x, that.me.user.y, targetX, targetY);
//             // that.me.moveTarget(targetX, targetY);
//         });
//     };
//
//     Agar.prototype.runTimer = function() {
//         this.context.clearRect(0,0, agar.canvasWidth, agar.canvasHeight);
//         let that = this;
//         this.socket.send(this.me.targetX + " " + this.me.targetY);
//         $.each(this.players, function(key, player) {
//             player.move();
//         });
//         this.me.move();
//         setTimeout(function(e){
//             that.runTimer();
//         },10);
//     };
//
//     $(document).ready(function(){
//         agar = new Agar();
//         $(window).bind('resize', function (){
//             agar.resize();
//         });
//     });
//
// })(jQuery);
//
