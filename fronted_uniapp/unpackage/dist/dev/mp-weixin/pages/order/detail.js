(global["webpackJsonp"] = global["webpackJsonp"] || []).push([["pages/order/detail"],{

/***/ 87:
/*!****************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/main.js?{"page":"pages%2Forder%2Fdetail"} ***!
  \****************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(wx, createPage) {

var _interopRequireDefault = __webpack_require__(/*! @babel/runtime/helpers/interopRequireDefault */ 4);
__webpack_require__(/*! uni-pages */ 26);
var _vue = _interopRequireDefault(__webpack_require__(/*! vue */ 25));
var _detail = _interopRequireDefault(__webpack_require__(/*! ./pages/order/detail.vue */ 88));
// @ts-ignore
wx.__webpack_require_UNI_MP_PLUGIN__ = __webpack_require__;
createPage(_detail.default);
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/wx.js */ 1)["default"], __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/index.js */ 2)["createPage"]))

/***/ }),

/***/ 88:
/*!*********************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/order/detail.vue ***!
  \*********************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _detail_vue_vue_type_template_id_57d42baa_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./detail.vue?vue&type=template&id=57d42baa&scoped=true& */ 89);
/* harmony import */ var _detail_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./detail.vue?vue&type=script&lang=js& */ 91);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _detail_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _detail_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__[key]; }) }(__WEBPACK_IMPORT_KEY__));
/* harmony import */ var _detail_vue_vue_type_style_index_0_id_57d42baa_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./detail.vue?vue&type=style&index=0&id=57d42baa&lang=scss&scoped=true& */ 93);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/runtime/componentNormalizer.js */ 32);

var renderjs





/* normalize component */

var component = Object(_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_3__["default"])(
  _detail_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__["default"],
  _detail_vue_vue_type_template_id_57d42baa_scoped_true___WEBPACK_IMPORTED_MODULE_0__["render"],
  _detail_vue_vue_type_template_id_57d42baa_scoped_true___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"],
  false,
  null,
  "57d42baa",
  null,
  false,
  _detail_vue_vue_type_template_id_57d42baa_scoped_true___WEBPACK_IMPORTED_MODULE_0__["components"],
  renderjs
)

component.options.__file = "pages/order/detail.vue"
/* harmony default export */ __webpack_exports__["default"] = (component.exports);

/***/ }),

/***/ 89:
/*!****************************************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/order/detail.vue?vue&type=template&id=57d42baa&scoped=true& ***!
  \****************************************************************************************************************************/
/*! exports provided: render, staticRenderFns, recyclableRender, components */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_template_id_57d42baa_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--17-0!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/template.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-uni-app-loader/page-meta.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./detail.vue?vue&type=template&id=57d42baa&scoped=true& */ 90);
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "render", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_template_id_57d42baa_scoped_true___WEBPACK_IMPORTED_MODULE_0__["render"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_template_id_57d42baa_scoped_true___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "recyclableRender", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_template_id_57d42baa_scoped_true___WEBPACK_IMPORTED_MODULE_0__["recyclableRender"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "components", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_template_id_57d42baa_scoped_true___WEBPACK_IMPORTED_MODULE_0__["components"]; });



/***/ }),

/***/ 90:
/*!****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--17-0!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/template.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-uni-app-loader/page-meta.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/order/detail.vue?vue&type=template&id=57d42baa&scoped=true& ***!
  \****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! exports provided: render, staticRenderFns, recyclableRender, components */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "render", function() { return render; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return staticRenderFns; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "recyclableRender", function() { return recyclableRender; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "components", function() { return components; });
var components
var render = function () {
  var _vm = this
  var _h = _vm.$createElement
  var _c = _vm._self._c || _h
}
var recyclableRender = false
var staticRenderFns = []
render._withStripped = true



/***/ }),

/***/ 91:
/*!**********************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/order/detail.vue?vue&type=script&lang=js& ***!
  \**********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/babel-loader/lib!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--13-1!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/script.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./detail.vue?vue&type=script&lang=js& */ 92);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__[key]; }) }(__WEBPACK_IMPORT_KEY__));
 /* harmony default export */ __webpack_exports__["default"] = (_A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 92:
/*!*****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/babel-loader/lib!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--13-1!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/script.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/order/detail.vue?vue&type=script&lang=js& ***!
  \*****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(uni) {

var _interopRequireDefault = __webpack_require__(/*! @babel/runtime/helpers/interopRequireDefault */ 4);
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _regenerator = _interopRequireDefault(__webpack_require__(/*! @babel/runtime/regenerator */ 39));
var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__(/*! @babel/runtime/helpers/asyncToGenerator */ 41));
var _request = __webpack_require__(/*! @/utils/request */ 42);
var _user = __webpack_require__(/*! @/stores/user */ 52);
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
var _default = {
  data: function data() {
    return {
      order: {
        id: 1,
        orderNo: 'TF8829310',
        status: 1,
        statusText: '进行中',
        title: '臻托平台UI原型设计',
        amount: '3,500'
      },
      timeline: []
    };
  },
  mounted: function mounted() {
    var pages = getCurrentPages();
    var currentPage = pages[pages.length - 1];
    var id = currentPage.options.id;
    if (id) {
      this.order.id = id;
      this.loadOrderDetail(id);
    }
  },
  onShow: function onShow() {
    // 每次页面显示时重新加载订单数据（从签署页面返回时会触发）
    if (this.order.id) {
      this.loadOrderDetail(this.order.id);
    }
  },
  methods: {
    loadOrderDetail: function loadOrderDetail(id) {
      var _this = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee() {
        var res, data;
        return _regenerator.default.wrap(function _callee$(_context) {
          while (1) {
            switch (_context.prev = _context.next) {
              case 0:
                _context.prev = 0;
                _context.next = 3;
                return (0, _request.request)({
                  url: "/order/".concat(id),
                  method: 'GET'
                });
              case 3:
                res = _context.sent;
                if (res.data) {
                  data = res.data;
                  _this.order = {
                    id: data.order.id,
                    orderNo: data.order.orderNo,
                    status: data.order.status,
                    statusText: _this.getStatusText(data.order.status),
                    title: data.order.title,
                    amount: _this.formatCurrency(data.order.totalAmount),
                    role: _this.getUserRole(data.order)
                  };
                  _this.buildTimeline(data);
                }
                _context.next = 11;
                break;
              case 7:
                _context.prev = 7;
                _context.t0 = _context["catch"](0);
                console.error(_context.t0);
                uni.showToast({
                  title: '加载失败',
                  icon: 'none'
                });
              case 11:
              case "end":
                return _context.stop();
            }
          }
        }, _callee, null, [[0, 7]]);
      }))();
    },
    getUserRole: function getUserRole(order) {
      var uid = (0, _user.useUserStore)().userInfo.id;
      if (order.buyerId === uid) return 'buyer';
      if (order.sellerId === uid) return 'seller';
      return 'visitor';
    },
    buildTimeline: function buildTimeline(data) {
      var _this2 = this;
      var list = [];
      var order = data.order;

      // 1. 资金托管/发布
      list.push({
        title: '资金已托管',
        desc: this.formatDate(order.createTime),
        done: true,
        active: false
      });

      // 2. 商家接单
      if (order.status >= 8 && order.sellerId) {
        // 8-待签约及后续
        list.push({
          title: '商家已接单',
          desc: '服务商已确认接单',
          done: true,
          active: false
        });
      }

      // 3. 待签署合同 (status = 8)
      if (order.status === 8) {
        // 检查是否已有合同
        var hasContract = data.contract && data.contract.id;
        if (!hasContract && this.order.role === 'seller') {
          // 商家需要先创建合同
          list.push({
            title: '创建项目合同',
            desc: '请创建并提交项目合同',
            done: false,
            active: true,
            actions: ['创建合同'],
            orderId: order.id
          });
        } else if (hasContract) {
          // 合同已创建，等待签署
          var contractStatus = data.contract.status;
          // 0-草稿 1-待甲方签署 2-待乙方签署 3-已生效

          if (contractStatus === 1 && this.order.role === 'buyer') {
            list.push({
              title: '待您签署合同',
              desc: '商家已创建合同，请查看并签署',
              done: false,
              active: true,
              actions: ['查看并签署'],
              orderId: order.id,
              contractId: data.contract.id
            });
          } else if (contractStatus === 2 && this.order.role === 'seller') {
            list.push({
              title: '待您签署合同',
              desc: '买家已签署，请您签署确认',
              done: false,
              active: true,
              actions: ['查看并签署'],
              orderId: order.id,
              contractId: data.contract.id
            });
          } else if (contractStatus === 1 && this.order.role === 'seller') {
            list.push({
              title: '等待买家签署',
              desc: '合同已提交，等待买家签署',
              done: false,
              active: true
            });
          } else if (contractStatus === 2 && this.order.role === 'buyer') {
            list.push({
              title: '等待商家签署',
              desc: '您已签署，等待商家签署',
              done: false,
              active: true
            });
          }
        } else if (this.order.role === 'buyer') {
          // 买家等待商家创建合同
          list.push({
            title: '等待商家创建合同',
            desc: '商家正在准备项目合同',
            done: false,
            active: true
          });
        }
      }

      // 4. 阶段信息 (status >= 2)
      if (order.status >= 2 && data.stages && data.stages.length > 0) {
        data.stages.forEach(function (stage) {
          list.push({
            title: stage.stageName,
            desc: stage.status === 1 ? '进行中' : stage.status >= 2 ? '已完成' : '待开始',
            done: stage.status >= 2,
            // 2-Delivered, 3-Completed
            active: stage.status === 1,
            // 1-Processing
            actions: _this2.getStageActions(stage, _this2.order.role),
            stageId: stage.id,
            orderId: order.id
          });
        });
      }

      // 5. 验收完成
      if (order.status === 4) {
        // 4-Completed
        list.push({
          title: '订单完成',
          desc: this.formatDate(order.updateTime),
          done: true,
          active: true
        });
      }
      this.timeline = list;
    },
    getStageActions: function getStageActions(stage, role) {
      // 根据角色和阶段状态返回操作
      // stage status: 0-Pending, 1-Processing, 2-Delivery(Wait Accept), 3-Completed, 4-Escrowed(Wait Start)
      if (role === 'buyer' && stage.status === 2) {
        // 待验收状态
        return ['确认验收', '拒绝验收'];
      }
      if (role === 'seller' && (stage.status === 1 || stage.status === 0 || stage.status === 4)) {
        // 这里简化逻辑，实际需要看具体业务允许何时提交
        if (stage.status === 1 || stage.status === 4) return ['提交交付'];
      }
      return [];
    },
    getStatusText: function getStatusText(status) {
      var map = {
        0: '待接单',
        8: '待签约',
        1: '待托管',
        2: '进行中',
        3: '待验收',
        9: '质保中',
        4: '已完成',
        5: '已取消',
        6: '纠纷中'
      };
      return map[status] || '未知';
    },
    formatCurrency: function formatCurrency(value) {
      if (!value) return '0.00';
      return parseFloat(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
      });
    },
    formatDate: function formatDate(dateStr) {
      if (!dateStr) return '';
      return dateStr.substring(0, 16).replace('T', ' ');
    },
    goBack: function goBack() {
      uni.navigateBack();
    },
    handleAction: function handleAction(action, step) {
      var _this3 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee6() {
        var orderId, contractId, stageId;
        return _regenerator.default.wrap(function _callee6$(_context6) {
          while (1) {
            switch (_context6.prev = _context6.next) {
              case 0:
                orderId = _this3.order.id;
                if (!(action === '创建合同')) {
                  _context6.next = 4;
                  break;
                }
                // 跳转到合同创建页面
                uni.navigateTo({
                  url: "/pages/user/contract-edit?orderId=".concat(orderId)
                });
                return _context6.abrupt("return");
              case 4:
                if (!(action === '查看并签署')) {
                  _context6.next = 11;
                  break;
                }
                // 跳转到合同签署页面
                contractId = step.contractId;
                if (contractId) {
                  _context6.next = 9;
                  break;
                }
                uni.showToast({
                  title: '合同ID缺失',
                  icon: 'none'
                });
                return _context6.abrupt("return");
              case 9:
                uni.navigateTo({
                  url: "/pages/user/contract-sign?orderId=".concat(orderId, "&contractId=").concat(contractId)
                });
                return _context6.abrupt("return");
              case 11:
                if (!(action === '签署合同')) {
                  _context6.next = 14;
                  break;
                }
                uni.showModal({
                  title: '签署合同',
                  content: '请确认您已阅读并同意合同条款。签署后将进入项目执行阶段。',
                  confirmText: '确认签署',
                  success: function () {
                    var _success = (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee2(res) {
                      var contractRes, _contractId;
                      return _regenerator.default.wrap(function _callee2$(_context2) {
                        while (1) {
                          switch (_context2.prev = _context2.next) {
                            case 0:
                              if (!res.confirm) {
                                _context2.next = 20;
                                break;
                              }
                              uni.showLoading({
                                title: '签署中...'
                              });
                              _context2.prev = 2;
                              _context2.next = 5;
                              return (0, _request.request)({
                                url: "/contract/order/".concat(orderId),
                                method: 'GET'
                              });
                            case 5:
                              contractRes = _context2.sent;
                              if (!(!contractRes.data || !contractRes.data.id)) {
                                _context2.next = 8;
                                break;
                              }
                              throw new Error('合同不存在，请联系客服');
                            case 8:
                              _contractId = contractRes.data.id; // 2. 签署合同
                              _context2.next = 11;
                              return (0, _request.request)({
                                url: "/contract/".concat(_contractId, "/sign"),
                                method: 'POST',
                                data: {
                                  signature: 'merchant_signature' // 商家签名
                                }
                              });
                            case 11:
                              uni.hideLoading();
                              uni.showToast({
                                title: '签署成功',
                                icon: 'success'
                              });

                              // 3. 重新加载订单详情
                              setTimeout(function () {
                                _this3.loadOrderDetail(orderId);
                              }, 1500);
                              _context2.next = 20;
                              break;
                            case 16:
                              _context2.prev = 16;
                              _context2.t0 = _context2["catch"](2);
                              uni.hideLoading();
                              uni.showModal({
                                title: '签署失败',
                                content: _context2.t0.message || '签署失败，请稍后重试',
                                showCancel: false
                              });
                            case 20:
                            case "end":
                              return _context2.stop();
                          }
                        }
                      }, _callee2, null, [[2, 16]]);
                    }));
                    function success(_x) {
                      return _success.apply(this, arguments);
                    }
                    return success;
                  }()
                });
                return _context6.abrupt("return");
              case 14:
                if (!(!step || !step.stageId)) {
                  _context6.next = 16;
                  break;
                }
                return _context6.abrupt("return");
              case 16:
                stageId = step.stageId;
                if (action === '提交交付') {
                  uni.showModal({
                    title: '提交交付',
                    content: '确认提交当前阶段成果？',
                    editable: true,
                    placeholderText: '请输入成果描述或链接',
                    success: function () {
                      var _success2 = (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee3(res) {
                        return _regenerator.default.wrap(function _callee3$(_context3) {
                          while (1) {
                            switch (_context3.prev = _context3.next) {
                              case 0:
                                if (!res.confirm) {
                                  _context3.next = 11;
                                  break;
                                }
                                _context3.prev = 1;
                                _context3.next = 4;
                                return (0, _request.request)({
                                  url: "/order/".concat(orderId, "/stage/").concat(stageId, "/deliver"),
                                  method: 'POST',
                                  data: {
                                    evidenceUrl: 'https://oss.trust.com/demo.zip',
                                    deliveryDesc: res.content || '已完成阶段工作，请查收'
                                  }
                                });
                              case 4:
                                uni.showToast({
                                  title: '提交成功'
                                });
                                _this3.loadOrderDetail(orderId);
                                _context3.next = 11;
                                break;
                              case 8:
                                _context3.prev = 8;
                                _context3.t0 = _context3["catch"](1);
                                uni.showToast({
                                  title: _context3.t0.message,
                                  icon: 'none'
                                });
                              case 11:
                              case "end":
                                return _context3.stop();
                            }
                          }
                        }, _callee3, null, [[1, 8]]);
                      }));
                      function success(_x2) {
                        return _success2.apply(this, arguments);
                      }
                      return success;
                    }()
                  });
                } else if (action === '确认验收') {
                  uni.showModal({
                    title: '确认验收',
                    content: '确认验收通过该阶段成果？资金将释放给商家。',
                    success: function () {
                      var _success3 = (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee4(res) {
                        return _regenerator.default.wrap(function _callee4$(_context4) {
                          while (1) {
                            switch (_context4.prev = _context4.next) {
                              case 0:
                                if (!res.confirm) {
                                  _context4.next = 11;
                                  break;
                                }
                                _context4.prev = 1;
                                _context4.next = 4;
                                return (0, _request.request)({
                                  url: "/order/".concat(orderId, "/stage/").concat(stageId, "/accept"),
                                  method: 'POST'
                                });
                              case 4:
                                uni.showToast({
                                  title: '验收成功'
                                });
                                _this3.loadOrderDetail(orderId);
                                _context4.next = 11;
                                break;
                              case 8:
                                _context4.prev = 8;
                                _context4.t0 = _context4["catch"](1);
                                uni.showToast({
                                  title: _context4.t0.message,
                                  icon: 'none'
                                });
                              case 11:
                              case "end":
                                return _context4.stop();
                            }
                          }
                        }, _callee4, null, [[1, 8]]);
                      }));
                      function success(_x3) {
                        return _success3.apply(this, arguments);
                      }
                      return success;
                    }()
                  });
                } else if (action === '拒绝验收') {
                  uni.showModal({
                    title: '拒绝验收',
                    content: '请输入拒绝原因',
                    editable: true,
                    placeholderText: '原因',
                    success: function () {
                      var _success4 = (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee5(res) {
                        return _regenerator.default.wrap(function _callee5$(_context5) {
                          while (1) {
                            switch (_context5.prev = _context5.next) {
                              case 0:
                                if (!res.confirm) {
                                  _context5.next = 11;
                                  break;
                                }
                                _context5.prev = 1;
                                _context5.next = 4;
                                return (0, _request.request)({
                                  url: "/order/".concat(orderId, "/stage/").concat(stageId, "/reject"),
                                  method: 'POST',
                                  data: {
                                    reason: res.content || '成果不符合要求'
                                  }
                                });
                              case 4:
                                uni.showToast({
                                  title: '已拒绝'
                                });
                                _this3.loadOrderDetail(orderId);
                                _context5.next = 11;
                                break;
                              case 8:
                                _context5.prev = 8;
                                _context5.t0 = _context5["catch"](1);
                                uni.showToast({
                                  title: _context5.t0.message,
                                  icon: 'none'
                                });
                              case 11:
                              case "end":
                                return _context5.stop();
                            }
                          }
                        }, _callee5, null, [[1, 8]]);
                      }));
                      function success(_x4) {
                        return _success4.apply(this, arguments);
                      }
                      return success;
                    }()
                  });
                } else {
                  uni.showToast({
                    title: action,
                    icon: 'none'
                  });
                }
              case 18:
              case "end":
                return _context6.stop();
            }
          }
        }, _callee6);
      }))();
    }
  }
};
exports.default = _default;
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/index.js */ 2)["default"]))

/***/ }),

/***/ 93:
/*!*******************************************************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/order/detail.vue?vue&type=style&index=0&id=57d42baa&lang=scss&scoped=true& ***!
  \*******************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_style_index_0_id_57d42baa_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/mini-css-extract-plugin/dist/loader.js??ref--8-oneOf-1-0!./node_modules/css-loader/dist/cjs.js??ref--8-oneOf-1-1!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/stylePostLoader.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-2!./node_modules/postcss-loader/src??ref--8-oneOf-1-3!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/sass-loader/dist/cjs.js??ref--8-oneOf-1-4!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-5!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./detail.vue?vue&type=style&index=0&id=57d42baa&lang=scss&scoped=true& */ 94);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_style_index_0_id_57d42baa_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_style_index_0_id_57d42baa_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_style_index_0_id_57d42baa_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_style_index_0_id_57d42baa_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__[key]; }) }(__WEBPACK_IMPORT_KEY__));
 /* harmony default export */ __webpack_exports__["default"] = (_A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_detail_vue_vue_type_style_index_0_id_57d42baa_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 94:
/*!***********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/mini-css-extract-plugin/dist/loader.js??ref--8-oneOf-1-0!./node_modules/css-loader/dist/cjs.js??ref--8-oneOf-1-1!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/stylePostLoader.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-2!./node_modules/postcss-loader/src??ref--8-oneOf-1-3!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/sass-loader/dist/cjs.js??ref--8-oneOf-1-4!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-5!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/order/detail.vue?vue&type=style&index=0&id=57d42baa&lang=scss&scoped=true& ***!
  \***********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

// extracted by mini-css-extract-plugin
    if(false) { var cssReload; }
  

/***/ })

},[[87,"common/runtime","common/vendor"]]]);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/detail.js.map