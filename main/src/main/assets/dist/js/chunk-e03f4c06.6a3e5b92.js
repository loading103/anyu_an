(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-e03f4c06"],{"0962":function(t,e,i){},"0d48":function(t,e,i){t.exports=i.p+"img/6ffe86_187x74.d46ac5a9.png"},2539:function(t,e,i){var n=i("4ea4");i("a4d3"),i("e01a"),i("4160"),i("b0c0"),i("159b"),Object.defineProperty(e,"__esModule",{value:!0}),e.default=void 0;var a=n(i("5530")),s=i("2f62"),o=(n(i("1157")),i("d194")),r=i("e27c"),c=i("cbb9"),h={data:function(){return{gameList:!1,config:{},UserSession:{},dml:"",ref:-1,money:""}},watch:{},computed:(0,a.default)({},(0,s.mapState)("Game",{UserBaseInfo:function(t){return t[o.GAME_GET_USER_BASE_INFO]}})),created:function(){this.config=this.$route.query,this.getUserBaseInfo(),this.getGameList();var t=this.$route.query,e=t.uid,i=t.token;this.config.uid=e,this.config.token=i;var n=this.$user();if(this.$store.commit("User/SET_USER",n),this.env.app){var a='{"title":"额度转换","showNai":"y","back":"app"}';this.callHandle(a)}},mounted:function(){this.getUserSession()},methods:{getUserSession:function(){var t=this;this.$api.user.getUserSession(this.baseUser).then((function(e){t.dml=e.drawingMoney,t.UserSession=e}))},getUserSession1:function(){var t=this,e=this;this.$api.user.getUserSession(this.baseUser).then((function(i){e.dml=i.drawingMoney,e.ref=-1,t.$toast("刷新完成")}))},whatIsThis:function(){this.mui.confirm("&nbsp;&nbsp;&nbsp;提款打码量，是指可以结算的提款数量，每笔投注都会计算打码量。\n&nbsp;&nbsp;&nbsp;例：投注100元，即产生100元提款打码量。","温馨提示",["了解"])},tiaozhuan:function(){this.$router.push("/capitalRecord1?uid="+this.config.uid+"&token="+this.config.token)},tiaozhuan1:function(){this.$router.push("/capitalRecord?uid="+this.config.uid+"&token="+this.config.token)},tiaozhuan2:function(){this.$router.push("/recharge?uid="+this.config.uid+"&token="+this.config.token)},tiaozhuan3:function(){this.$router.push("/withdrawal?uid="+this.config.uid+"&token="+this.config.token)},to_change:function(t,e){var i=arguments.length>2&&void 0!==arguments[2]&&arguments[2],n=arguments.length>3&&void 0!==arguments[3]?arguments[3]:0;if(n<=1&&!e)return this.$toast("游戏余额不足"),!1;switch(this.money=parseInt(n),t){case"1000001359742941":return this.kaiyuan(e,i);case"1000000470622605":return this.vg(e,i);case"1000001350416944":return this.fg(e,i);case"1000001350416946":return this.junbo(e,i);default:return 0}},importImg:function(t){switch(t){case"1000001359742941":return i("3bb5");case"1000000470622605":return i("b2d2");case"1000001350416944":return i("2607");case"1000001350416946":return i("8047");default:return i("0d48")}},resref:function(t,e){switch(t){case"1000001359742941":this.getAllAmount(!0),this.ref=e;break;case"1000000470622605":this.getVgAmount(!0),this.ref=e;break;case"1000001350416944":this.getLyAmount(!0),this.ref=e;break;case"1000001350416946":this.getMyBalance(!0),this.ref=e;break;case"111":this.getUserBaseInfo(!0),this.ref=e;break;case"222":this.getUserSession1(),this.ref=e;break;default:break}},toggleTabs:function(t){this.tabs=t,this.getNetData()},getNetData:function(){var t=this;this.list=!1;var e=(0,a.default)({},this.config,{pageIndex:1,pageSize:20,type:""});switch(this.tabs){case 1:e.startTime=(0,r.getToday)(),e.endTime=(0,r.getTomorrow)();break;case 2:e.startTime=(0,r.lastDay)(),e.endTime=(0,r.getToday)();break;case 3:var i=(0,r.thisMonth)();e.startTime=i.thisMonthStart,e.endTime=i.thisMonthEnd;break;default:break}this.$api.transformation.getLogUserCoinListBySC(e).then((function(e){console.log(e),setTimeout((function(){t.list=e.logUserCoinList}),1200)}))},money_addend:function(t){if(this.money_tabs=t,t)this.money=1*this.money+this.add_list[t];else if(this.is)this.money=parseInt(this.UserBaseInfo.balance);else{var e=parseInt(this.actions[this.select].price);this.money=e>=0?e:0}},onSelect:function(t){this.show=!1,this.select=t.index,this.$toast("切换到 => ".concat(t.name))},onCancel:function(){this.$toast("取消")},getUserBaseInfo:function(t){var e=this;t&&(this.ref=4);var i=this.config,n=i.uid,a=i.token,s={uid:n,token:a};this.$store.dispatch("Game/".concat(o.GAME_GET_USER_BASE_INFO),s).then((function(){t&&(e.$toast("刷新完成"),e.ref=-1)}))},getMyBalance:function(t){var e=this;this.$api.transformation.getMyBalance(this.config).then((function(i){var n=e.getIndex("1000001350416946");1==i.result&&(e.gameList[n].money=i.data>=0?i.data:"获取失败",t&&(e.ref=-1,e.$toast("刷新完成")))}))},getAllAmount:function(t){var e=this;this.$api.transformation.getAllAmount(this.config).then((function(i){var n=e.getIndex("1000001359742941");e.gameList[n].money=i.chessAmount>=0?i.chessAmount:"获取失败",t&&(e.ref=-1,e.$toast("刷新完成"))}))},getVgAmount:function(t){var e=this;this.$api.transformation.getVgAmount(this.config).then((function(i){console.log(i);var n=e.getIndex("1000000470622605");e.gameList[n].money=i.data>=0?i.data:"获取失败",t&&(e.ref=-1,e.$toast("刷新完成"))}))},getLyAmount:function(){var t=this;this.$api.transformation.getLyAmount(this.config).then((function(e){console.log(e);var i=t.getIndex("1000001350416944");t.gameList[i].money=e.data>=0?e.data:"获取失败",t.ref=-1}))},getGameList:function(){var t=this;this.$api.transformation.getGameList().then((function(e){console.log(e);var i=e.data;i.forEach((function(t){switch(t.money=0,t.show=!0,t.gameCode){case"1000001359742941":t.gameName="开元红包捕鱼";break;case"1000001350416944":t.gameName="FG捕鱼";break;case"1000000470622605":t.gameName="财神捕鱼";break;case"1000001350416946":t.show=!1;break;default:break}})),t.gameList=i,t.getMyBalance(),t.getAllAmount(),t.getVgAmount(),t.getLyAmount()}))},getIndex:function(t){var e=-1;return this.gameList.forEach((function(i,n){i.gameCode==t&&(e=n)})),e},to_ky:function(){var t=this.actions[this.select];if(console.log(t),console.log(this.is),""==this.money)return this.$toast("请输入金额"),!1;this.is?this.UserBaseInfo.balance>=this.money?this.to_change(t.code,!0,!1):this.$toast("转入的金额不能大于自己的余额"):t.price>=this.money?this.to_change(t.code,!1,!1):this.$toast("转入的金额不能大于游戏的余额")},kaiyuan:function(t,e){var i=this,n=(0,a.default)({},this.config);t?this.UserBaseInfo.balance>0?(this.$toast.loading({duration:0,forbidClick:!0,message:"转换中..."}),n.userBalance=e?this.UserBaseInfo.balance:this.money,this.$api.transformation.changeAmount(n).then((function(t){i.$toast(t.description),i.getAllAmount(),i.getUserBaseInfo()}))):this.$toast("余额不足"):(this.$toast.loading({duration:0,forbidClick:!0,message:"转换中..."}),n.chessAmount=this.money,this.$api.transformation.unChangeAmount(n).then((function(t){i.$toast(t.description),i.getAllAmount(),i.getUserBaseInfo()})))},junbo:function(t,e){var i=this,n=(0,a.default)({},this.config,{merchantName:c.companyShortName});t?this.UserBaseInfo.balance>0?(this.$toast.loading({duration:0,forbidClick:!0,message:"转换中..."}),n.money=e?this.UserBaseInfo.balance:this.money,this.$api.transformation.upToJb(n).then((function(t){i.$toast(t.description),i.getUserBaseInfo(),i.getMyBalance()}))):this.$toast("余额不足"):(this.$toast.loading({duration:0,forbidClick:!0,message:"转换中..."}),n.money=this.money,this.$api.transformation.downFromJb(n).then((function(t){i.$toast(t.description),i.getMyBalance(),i.getUserBaseInfo()})))},vg:function(t,e){var i=this;console.log(t,e);var n=(0,a.default)({},this.config);t?this.UserBaseInfo.balance>0?(this.$toast.loading({duration:0,forbidClick:!0,message:"转换中..."}),n.money=e?parseInt(this.UserBaseInfo.balance):this.money,this.$api.transformation.upToVg(n).then((function(t){i.$toast(t.description),i.getVgAmount(),i.getUserBaseInfo()}))):this.$toast("余额不足"):(this.$toast.loading({duration:0,forbidClick:!0,message:"转换中..."}),n.money=this.money,this.$api.transformation.downVg(n).then((function(t){i.$toast(t.description),i.getVgAmount(),i.getUserBaseInfo()})))},fg:function(t,e){var i=this;console.log(t),console.log(t,e);var n=(0,a.default)({},this.config);t?this.UserBaseInfo.balance>0?(this.$toast.loading({duration:0,forbidClick:!0,message:"转换中..."}),n.money=e?parseInt(this.UserBaseInfo.balance):this.money,this.$api.transformation.upToFg(n).then((function(t){i.$toast(t.description),i.getLyAmount(),i.getUserBaseInfo()}))):this.$toast("余额不足"):(this.$toast.loading({duration:0,forbidClick:!0,message:"转换中..."}),n.money=this.money,this.$api.transformation.downFg(n).then((function(t){i.$toast(t.description),i.getLyAmount(),i.getUserBaseInfo()})))}},filters:{operType:function(t){var e="";switch(t){case 26:e="开元转余额";break;case 27:e="余额转开元";break;case 38:e="余额转乐游";break;case 42:e="余额转VG";break;case 43:e="VG转余额";break;case 56:e="彩票转余额";break;case 57:e="余额转彩票";break;default:break}return e}}};e.default=h},2607:function(t,e,i){t.exports=i.p+"img/d121c6_340x340.bab4471b.png"},"2bc6":function(t,e,i){"use strict";var n=i("0962"),a=i.n(n);a.a},3306:function(t,e){t.exports="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAATCAYAAAByUDbMAAABcklEQVQ4jY2UvUpDQRCFj0n8ASWVMbEVf0oRbLQR4l/tm4ivYS0GK7GyUXyEvIMpBC1ELIIGIWiMBOMnI3vlZt298cDC3pnZM3t3zswIoAjykkqSim7fl9SW1HL7vzAyb1WAGtAhjDfnL/tnfaJd4CVC4qMFbMbIjKgXONQA6sBNwNdLEyZEs4EbPQCrwDiwAIwC68Bj4IblNNlJIOsasAI8ue8msOzsPmoJWR7oes5bl+TCs587e9OzW1HyBVf+Ca/M804CRc/elTQmacqzT0qatixL0XoN4hooAQcR/2JOUiGm2hQakqqSdiQdxkRuZJ9DiL4k7UnakHQmKReJ6xdciwy71Z2kywwiQ9ucz+5hY7DH3Zc0lxHT+eFxpa5lPPw7cA98ZMQcp0VbyejJKxdTj/itA2b83tyO9Ka11WlAqAaLr8amxpbL9B9Y3C9RiMyWNa29obVICK/AUfJr6ZU1aa3SyaQ1CZkek0lr2huEpG/gM1vFlj/VWQAAAABJRU5ErkJggg=="},"3bb5":function(t,e,i){t.exports=i.p+"img/b8d3db_60x47.d738041f.png"},"6b81":function(t,e,i){"use strict";i.r(e);var n=i("2539"),a=i.n(n);for(var s in n)"default"!==s&&function(t){i.d(e,t,(function(){return n[t]}))}(s);e["default"]=a.a},8047:function(t,e,i){t.exports=i.p+"img/060748_330x283.34ad9ade.png"},"8c92":function(t,e,i){"use strict";i.d(e,"a",(function(){return n})),i.d(e,"b",(function(){return a}));var n=function(){var t=this,e=t.$createElement,n=t._self._c||e;return t.gameList?n("div",{staticClass:"container back-two"},[n("div",{staticClass:"trans-form-page"},[n("div",{staticClass:"trans-form-card"},[n("div",{staticClass:"wrap",staticStyle:{height:"2.5rem"}},[n("div",{staticStyle:{width:"48%",float:"left","text-align":"center","border-right":"#CEC8D8 1px solid",padding:"0.1rem 0",margin:"0.1rem 0"}},[n("p",{staticStyle:{"font-size":"0.3rem",color:"#F5EFEF"}},[t._v("账户余额")]),n("div",{staticClass:"balanceBox",staticStyle:{color:"#fff",padding:"0.15rem 0"}},[t.UserBaseInfo.balance?n("b",{staticStyle:{"font-size":"0.4rem"}},[t._v(t._s(t._f("fixed")(t.UserBaseInfo.balance,3)))]):n("b",{staticStyle:{"font-size":"0.4rem"}},[t._v("0.000")]),n("span",{class:["icon-refresh-gray",{acti:5===t.ref}],staticStyle:{position:"absolute",top:"1rem"},on:{click:function(e){return t.resref("111",5)}}})])]),n("div",{staticStyle:{width:"48%",float:"right","text-align":"center",padding:"0.1rem 0",margin:"0.1rem 0"}},[n("p",{staticStyle:{"font-size":"0.3rem",color:"#F5EFEF"}},[t._v("提款打码量")]),n("div",{staticClass:"balanceBox",staticStyle:{color:"#fff",padding:"0.15rem 0"}},[""!=t.dml?n("b",{staticStyle:{"font-size":"0.4rem"}},[t._v(t._s(t._f("fixed")(t.dml,3)))]):n("b",{staticStyle:{"font-size":"0.4rem"}},[t._v("0.000")]),n("img",{staticStyle:{position:"absolute",top:"0.5rem",height:"0.3rem",right:"0.9rem"},attrs:{src:i("3306")},on:{click:t.whatIsThis}}),n("span",{class:["icon-refresh-gray",{acti:6===t.ref}],staticStyle:{position:"absolute",top:"1rem"},on:{click:function(e){return t.resref("222",6)}}})])]),n("div",{staticClass:"btnBox",staticStyle:{color:"#F5F4F4"}},[n("button",{staticStyle:{width:"1.5rem",height:"0.6rem",margin:"0 0.1rem",border:"1px solid #F5F4F4","border-radius":"0.3rem"},on:{click:t.tiaozhuan}},[t._v("转换记录")]),n("button",{staticStyle:{width:"1.5rem",height:"0.6rem",margin:"0 0.1rem",border:"1px solid #F5F4F4","border-radius":"0.3rem"},on:{click:t.tiaozhuan1}},[t._v("资金明细")]),n("button",{staticStyle:{width:"1.5rem",height:"0.6rem",margin:"0 0.1rem",border:"1px solid #F5F4F4","border-radius":"0.3rem"},on:{click:t.tiaozhuan2}},[t._v("立即充值")]),n("button",{staticStyle:{width:"1.5rem",height:"0.6rem",margin:"0 0.1rem",border:"1px solid #F5F4F4","border-radius":"0.3rem"},on:{click:t.tiaozhuan3}},[t._v("现金提取")])])])]),n("div",{staticClass:"trans-form-page-list"},[n("ul",[t._l(t.gameList,(function(e,i){return[e.show?n("li",{key:i,class:"li"+i},[n("div",{staticClass:"img-wrap"},[n("img",{attrs:{src:t.importImg(e.gameCode),alt:""}}),n("h2",[t._v(t._s(e.gameName))])]),e.maintaining?t._e():n("div",{staticStyle:{height:"2rem",padding:"0 0.2rem"}},[n("div",{staticClass:"money",staticStyle:{width:"60%",float:"left","margin-top":"0.2rem",height:"0.5rem","line-height":"0.5rem"}},[n("h3",{staticStyle:{float:"left","margin-right":"0.1rem"}},[t._v(t._s(t._f("fixed")(e.money,3)))]),n("span",{class:["icon-refresh-gray",{acti:t.ref===i}],staticStyle:{float:"left"},on:{click:function(n){return t.resref(e.gameCode,i)}}})]),n("div",{staticClass:"change-",staticStyle:{width:"40%",float:"right","margin-top":"0.2rem"}},[n("div",[n("button",{staticStyle:{background:"-webkit-linear-gradient(left,#4a93ff,#30bfff)",width:"1.1rem",height:".5rem","border-radius":".5rem",color:"#fff"},on:{click:function(i){return t.to_change(e.gameCode,!0,!0)}}},[t._v("转入")]),n("button",{staticStyle:{background:"-webkit-linear-gradient(left,#4a93ff,#30bfff)",width:"1.1rem",height:".5rem","border-radius":".5rem",color:"#fff","margin-left":".2rem"},on:{click:function(i){return t.to_change(e.gameCode,!1,!0,e.money)}}},[t._v("转出")])])])]),e.maintaining?n("div",{staticStyle:{height:"2rem",padding:"0 0.2rem"}},[n("div",{staticClass:"maintaining"},[t._v("维护中")])]):t._e()]):t._e()]}))],2)])])]):n("loadding")},a=[]},"9d28":function(t,e,i){"use strict";i.r(e);var n=i("8c92"),a=i("6b81");for(var s in a)"default"!==s&&function(t){i.d(e,t,(function(){return a[t]}))}(s);i("2bc6");var o=i("2877"),r=Object(o["a"])(a["default"],n["a"],n["b"],!1,null,null,null);e["default"]=r.exports},b2d2:function(t,e,i){t.exports=i.p+"img/b58b9c_200x200.9c73c62a.png"}}]);
//# sourceMappingURL=chunk-e03f4c06.6a3e5b92.js.map