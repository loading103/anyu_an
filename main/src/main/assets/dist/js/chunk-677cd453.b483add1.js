(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-677cd453"],{"58c6":function(t,i,n){var e=n("4ea4");n("4de4"),Object.defineProperty(i,"__esModule",{value:!0}),i.default=void 0;var a=e(n("5530")),r=n("2f62"),s=n("d194"),c={data:function(){return{user:{uid:"",token:""},id:""}},created:function(){var t=this.$route.query.id;this.id=t,this.env.app&&(json='{"title":"活动详情","rightTitle":{"name":"","url":""},"showNai":"y","back":"h5"}',this.callHandle(json))},computed:(0,a.default)({},(0,r.mapState)("Activity",{promotionList:function(t){var i=this;return t[s.ACTIVITY_GET_PROMO_TION].promotionList.filter((function(t){return t.id==i.id}))}})),methods:{}};i.default=c},"7ed6":function(t,i,n){"use strict";n.r(i);var e=n("58c6"),a=n.n(e);for(var r in e)"default"!==r&&function(t){n.d(i,t,(function(){return e[t]}))}(r);i["default"]=a.a},"94b1":function(t,i,n){t.exports=n.p+"img/6daf52_137x135.bc743fc2.png"},a116:function(t,i,n){"use strict";n.d(i,"a",(function(){return e})),n.d(i,"b",(function(){return a}));var e=function(){var t=this,i=t.$createElement,e=t._self._c||i;return e("div",{staticClass:"container"},[e("div",{staticClass:"activity-page"},[e("ul",t._l(t.promotionList,(function(i){return e("li",{key:i.id},[e("h2",[t._v(t._s(i.name))]),i.startTime&&i.endTime?e("p",{staticClass:"p"},[t._v("活动时间："+t._s(t._f("timer1")(i.startTime))+" - "+t._s(t._f("timer1")(i.endTime)))]):e("p",{staticClass:"p"},[t._v("活动时间：永久")]),e("div",{staticClass:"pict"},[e("span",[t._v(t._s(i.smallImageId))]),e("image-load",{attrs:{url:i.url}})],1),e("div",{staticClass:"content",domProps:{innerHTML:t._s(i.content)}})])})),0)]),e("div",{staticClass:"fix-box",on:{click:function(i){return t.$router.back()}}},[e("img",{attrs:{src:n("94b1"),alt:""}})])])},a=[]},c2c2:function(t,i,n){"use strict";n.r(i);var e=n("a116"),a=n("7ed6");for(var r in a)"default"!==r&&function(t){n.d(i,t,(function(){return a[t]}))}(r);var s=n("2877"),c=Object(s["a"])(a["default"],e["a"],e["b"],!1,null,null,null);i["default"]=c.exports}}]);
//# sourceMappingURL=chunk-677cd453.b483add1.js.map