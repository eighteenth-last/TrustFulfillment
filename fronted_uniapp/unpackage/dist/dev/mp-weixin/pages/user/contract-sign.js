(global["webpackJsonp"] = global["webpackJsonp"] || []).push([["pages/user/contract-sign"],{

/***/ 143:
/*!**********************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/main.js?{"page":"pages%2Fuser%2Fcontract-sign"} ***!
  \**********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(wx, createPage) {

var _interopRequireDefault = __webpack_require__(/*! @babel/runtime/helpers/interopRequireDefault */ 4);
__webpack_require__(/*! uni-pages */ 26);
var _vue = _interopRequireDefault(__webpack_require__(/*! vue */ 25));
var _contractSign = _interopRequireDefault(__webpack_require__(/*! ./pages/user/contract-sign.vue */ 144));
// @ts-ignore
wx.__webpack_require_UNI_MP_PLUGIN__ = __webpack_require__;
createPage(_contractSign.default);
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/wx.js */ 1)["default"], __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/index.js */ 2)["createPage"]))

/***/ }),

/***/ 144:
/*!***************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-sign.vue ***!
  \***************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _contract_sign_vue_vue_type_template_id_102f0ee6_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./contract-sign.vue?vue&type=template&id=102f0ee6&scoped=true& */ 145);
/* harmony import */ var _contract_sign_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./contract-sign.vue?vue&type=script&lang=js& */ 147);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _contract_sign_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _contract_sign_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__[key]; }) }(__WEBPACK_IMPORT_KEY__));
/* harmony import */ var _contract_sign_vue_vue_type_style_index_0_id_102f0ee6_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./contract-sign.vue?vue&type=style&index=0&id=102f0ee6&lang=scss&scoped=true& */ 149);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/runtime/componentNormalizer.js */ 32);

var renderjs





/* normalize component */

var component = Object(_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_3__["default"])(
  _contract_sign_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__["default"],
  _contract_sign_vue_vue_type_template_id_102f0ee6_scoped_true___WEBPACK_IMPORTED_MODULE_0__["render"],
  _contract_sign_vue_vue_type_template_id_102f0ee6_scoped_true___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"],
  false,
  null,
  "102f0ee6",
  null,
  false,
  _contract_sign_vue_vue_type_template_id_102f0ee6_scoped_true___WEBPACK_IMPORTED_MODULE_0__["components"],
  renderjs
)

component.options.__file = "pages/user/contract-sign.vue"
/* harmony default export */ __webpack_exports__["default"] = (component.exports);

/***/ }),

/***/ 145:
/*!**********************************************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-sign.vue?vue&type=template&id=102f0ee6&scoped=true& ***!
  \**********************************************************************************************************************************/
/*! exports provided: render, staticRenderFns, recyclableRender, components */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_template_id_102f0ee6_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--17-0!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/template.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-uni-app-loader/page-meta.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./contract-sign.vue?vue&type=template&id=102f0ee6&scoped=true& */ 146);
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "render", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_template_id_102f0ee6_scoped_true___WEBPACK_IMPORTED_MODULE_0__["render"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_template_id_102f0ee6_scoped_true___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "recyclableRender", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_template_id_102f0ee6_scoped_true___WEBPACK_IMPORTED_MODULE_0__["recyclableRender"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "components", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_template_id_102f0ee6_scoped_true___WEBPACK_IMPORTED_MODULE_0__["components"]; });



/***/ }),

/***/ 146:
/*!**********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--17-0!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/template.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-uni-app-loader/page-meta.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-sign.vue?vue&type=template&id=102f0ee6&scoped=true& ***!
  \**********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
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
  var m0 = _vm.formatCurrency(_vm.contract.totalAmount)
  var m1 = _vm.contract.deliveryDeadline
    ? _vm.formatDate(_vm.contract.deliveryDeadline)
    : null
  var g0 = _vm.stages.length
  var l0 =
    g0 > 0
      ? _vm.__map(_vm.stages, function (stage, index) {
          var $orig = _vm.__get_orig(stage)
          var m2 = _vm.formatCurrency(stage.amount)
          return {
            $orig: $orig,
            m2: m2,
          }
        })
      : null
  _vm.$mp.data = Object.assign(
    {},
    {
      $root: {
        m0: m0,
        m1: m1,
        g0: g0,
        l0: l0,
      },
    }
  )
}
var recyclableRender = false
var staticRenderFns = []
render._withStripped = true



/***/ }),

/***/ 147:
/*!****************************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-sign.vue?vue&type=script&lang=js& ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/babel-loader/lib!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--13-1!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/script.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./contract-sign.vue?vue&type=script&lang=js& */ 148);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__[key]; }) }(__WEBPACK_IMPORT_KEY__));
 /* harmony default export */ __webpack_exports__["default"] = (_A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 148:
/*!***********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/babel-loader/lib!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--13-1!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/script.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-sign.vue?vue&type=script&lang=js& ***!
  \***********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
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
//
//
//
//
var _default = {
  data: function data() {
    return {
      orderId: null,
      contractId: null,
      contract: {},
      stages: [],
      agreed: false,
      userRole: '',
      // 'buyer' or 'seller'
      loading: false,
      // 签名相关
      hasSignature: false,
      signatureData: '',
      ctx: null,
      isDrawing: false,
      lastX: 0,
      lastY: 0,
      canvasWidth: 0,
      canvasHeight: 0
    };
  },
  computed: {
    canSign: function canSign() {
      var _userStore$userInfo;
      // 判断当前用户是否可以签署
      var userStore = (0, _user.useUserStore)();
      var userId = (_userStore$userInfo = userStore.userInfo) === null || _userStore$userInfo === void 0 ? void 0 : _userStore$userInfo.id;
      if (!userId || !this.contract.id) {
        console.log('canSign: false - 缺少userId或contractId');
        return false;
      }
      console.log('canSign 检查:', {
        userRole: this.userRole,
        contractStatus: this.contract.status,
        partyASignTime: this.contract.partyASignTime,
        partyBSignTime: this.contract.partyBSignTime
      });

      // 如果是买家且合同状态为1（待甲方签署），且甲方未签署
      if (this.userRole === 'buyer' && this.contract.status === 1 && !this.contract.partyASignTime) {
        console.log('canSign: true - 买家可以签署');
        return true;
      }

      // 如果是商家，在状态1或2时都可以签署（只要乙方还没签）
      if (this.userRole === 'seller' && (this.contract.status === 1 || this.contract.status === 2) && !this.contract.partyBSignTime) {
        console.log('canSign: true - 商家可以签署');
        return true;
      }
      console.log('canSign: false - 不满足签署条件');
      return false;
    },
    alreadySigned: function alreadySigned() {
      // 判断当前用户是否已签署
      if (this.userRole === 'buyer' && this.contract.partyASignTime) {
        return true;
      }
      if (this.userRole === 'seller' && this.contract.partyBSignTime) {
        return true;
      }
      return false;
    }
  },
  onLoad: function onLoad(options) {
    if (options.orderId && options.contractId) {
      this.orderId = options.orderId;
      this.contractId = options.contractId;
      this.loadData();
      this.initCanvas();
    } else {
      uni.showToast({
        title: '参数缺失',
        icon: 'none'
      });
      setTimeout(function () {
        return uni.navigateBack();
      }, 1500);
    }
  },
  methods: {
    loadData: function loadData() {
      var _this = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee() {
        var res, _userStore$userInfo2, userStore, userId, orderRes, order;
        return _regenerator.default.wrap(function _callee$(_context) {
          while (1) {
            switch (_context.prev = _context.next) {
              case 0:
                uni.showLoading({
                  title: '加载中...'
                });
                _context.prev = 1;
                _context.next = 4;
                return (0, _request.request)({
                  url: "/contract/".concat(_this.contractId),
                  method: 'GET'
                });
              case 4:
                res = _context.sent;
                console.log('合同详情:', res.data);
                if (!res.data) {
                  _context.next = 18;
                  break;
                }
                _this.contract = res.data;

                // 解析付款阶段（从 paymentTerms JSON 字符串）
                if (res.data.paymentTerms) {
                  try {
                    _this.stages = JSON.parse(res.data.paymentTerms);
                    console.log('付款阶段:', _this.stages);
                  } catch (e) {
                    console.error('解析付款阶段失败', e);
                    _this.stages = [];
                  }
                } else {
                  _this.stages = [];
                }

                // 判断用户角色
                userStore = (0, _user.useUserStore)();
                userId = (_userStore$userInfo2 = userStore.userInfo) === null || _userStore$userInfo2 === void 0 ? void 0 : _userStore$userInfo2.id;
                console.log('当前用户ID:', userId);
                console.log('甲方ID:', res.data.partyAId);
                console.log('乙方ID:', res.data.partyBId);

                // 通过订单信息判断角色
                _context.next = 16;
                return (0, _request.request)({
                  url: "/order/".concat(_this.orderId),
                  method: 'GET'
                });
              case 16:
                orderRes = _context.sent;
                if (orderRes.data) {
                  order = orderRes.data.order || orderRes.data;
                  if (order.buyerId === userId) {
                    _this.userRole = 'buyer';
                    console.log('用户角色: 买家');
                  } else if (order.sellerId === userId) {
                    _this.userRole = 'seller';
                    console.log('用户角色: 商家');
                  }
                }
              case 18:
                _context.next = 24;
                break;
              case 20:
                _context.prev = 20;
                _context.t0 = _context["catch"](1);
                console.error('加载合同失败', _context.t0);
                uni.showToast({
                  title: '加载失败',
                  icon: 'none'
                });
              case 24:
                _context.prev = 24;
                uni.hideLoading();
                return _context.finish(24);
              case 27:
              case "end":
                return _context.stop();
            }
          }
        }, _callee, null, [[1, 20, 24, 27]]);
      }))();
    },
    onAgreeChange: function onAgreeChange(e) {
      this.agreed = e.detail.value.includes('agree');
    },
    // 初始化画布
    initCanvas: function initCanvas() {
      // 获取系统信息
      var sysInfo = uni.getSystemInfoSync();
      this.canvasWidth = sysInfo.windowWidth - 64; // 减去左右padding
      this.canvasHeight = 400; // rpx转px大约是200rpx

      // 创建canvas上下文
      this.ctx = uni.createCanvasContext('signatureCanvas', this);

      // 设置画布样式
      this.ctx.setStrokeStyle('#1a1a2e');
      this.ctx.setLineWidth(3);
      this.ctx.setLineCap('round');
      this.ctx.setLineJoin('round');

      // 设置白色背景
      this.ctx.setFillStyle('#ffffff');
      this.ctx.fillRect(0, 0, this.canvasWidth, this.canvasHeight);
      this.ctx.draw();
    },
    // 开始绘制
    touchStart: function touchStart(e) {
      this.isDrawing = true;
      var touch = e.touches[0];
      this.lastX = touch.x;
      this.lastY = touch.y;
    },
    // 绘制中
    touchMove: function touchMove(e) {
      if (!this.isDrawing) return;
      var touch = e.touches[0];
      var x = touch.x;
      var y = touch.y;
      this.ctx.beginPath();
      this.ctx.moveTo(this.lastX, this.lastY);
      this.ctx.lineTo(x, y);
      this.ctx.stroke();
      this.ctx.draw(true); // 保留之前的内容

      this.lastX = x;
      this.lastY = y;
      this.hasSignature = true;
    },
    // 结束绘制
    touchEnd: function touchEnd() {
      if (this.isDrawing) {
        this.isDrawing = false;
        // 保存签名为图片
        this.saveSignature();
      }
    },
    // 保存签名
    saveSignature: function saveSignature() {
      var _this2 = this;
      uni.canvasToTempFilePath({
        canvasId: 'signatureCanvas',
        success: function success(res) {
          _this2.signatureData = res.tempFilePath;
        },
        fail: function fail(err) {
          console.error('保存签名失败', err);
        }
      }, this);
    },
    // 清除签名
    clearSignature: function clearSignature() {
      this.ctx.setFillStyle('#ffffff');
      this.ctx.fillRect(0, 0, this.canvasWidth, this.canvasHeight);
      this.ctx.draw();
      this.hasSignature = false;
      this.signatureData = '';
    },
    handleSign: function handleSign() {
      var _this3 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee3() {
        return _regenerator.default.wrap(function _callee3$(_context3) {
          while (1) {
            switch (_context3.prev = _context3.next) {
              case 0:
                if (_this3.agreed) {
                  _context3.next = 3;
                  break;
                }
                uni.showToast({
                  title: '请先阅读并同意合同条款',
                  icon: 'none'
                });
                return _context3.abrupt("return");
              case 3:
                if (_this3.hasSignature) {
                  _context3.next = 6;
                  break;
                }
                uni.showToast({
                  title: '请先签名',
                  icon: 'none'
                });
                return _context3.abrupt("return");
              case 6:
                uni.showModal({
                  title: '确认签署',
                  content: '签署后合同将生效，请确认您已仔细阅读所有条款',
                  confirmText: '确认签署',
                  success: function () {
                    var _success = (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee2(res) {
                      var base64;
                      return _regenerator.default.wrap(function _callee2$(_context2) {
                        while (1) {
                          switch (_context2.prev = _context2.next) {
                            case 0:
                              if (!res.confirm) {
                                _context2.next = 16;
                                break;
                              }
                              uni.showLoading({
                                title: '签署中...'
                              });
                              _context2.prev = 2;
                              _context2.next = 5;
                              return _this3.fileToBase64(_this3.signatureData);
                            case 5:
                              base64 = _context2.sent;
                              _context2.next = 8;
                              return (0, _request.request)({
                                url: "/contract/".concat(_this3.contractId, "/sign"),
                                method: 'POST',
                                data: {
                                  signature: base64
                                }
                              });
                            case 8:
                              uni.hideLoading();
                              uni.showModal({
                                title: '签署成功',
                                content: '合同已签署，项目即将开始',
                                showCancel: false,
                                success: function success() {
                                  // 返回订单详情页
                                  uni.navigateBack();
                                }
                              });
                              _context2.next = 16;
                              break;
                            case 12:
                              _context2.prev = 12;
                              _context2.t0 = _context2["catch"](2);
                              uni.hideLoading();
                              uni.showToast({
                                title: _context2.t0.message || '签署失败',
                                icon: 'none'
                              });
                            case 16:
                            case "end":
                              return _context2.stop();
                          }
                        }
                      }, _callee2, null, [[2, 12]]);
                    }));
                    function success(_x) {
                      return _success.apply(this, arguments);
                    }
                    return success;
                  }()
                });
              case 7:
              case "end":
                return _context3.stop();
            }
          }
        }, _callee3);
      }))();
    },
    // 文件转base64
    fileToBase64: function fileToBase64(filePath) {
      return new Promise(function (resolve, reject) {
        uni.getFileSystemManager().readFile({
          filePath: filePath,
          encoding: 'base64',
          success: function success(res) {
            resolve('data:image/png;base64,' + res.data);
          },
          fail: function fail(err) {
            reject(err);
          }
        });
      });
    },
    formatCurrency: function formatCurrency(value) {
      if (!value) return '0';
      return parseFloat(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
      });
    },
    formatDate: function formatDate(dateStr) {
      if (!dateStr) return '';
      var date = new Date(dateStr);
      return date.toLocaleDateString('zh-CN');
    }
  }
};
exports.default = _default;
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/index.js */ 2)["default"]))

/***/ }),

/***/ 149:
/*!*************************************************************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-sign.vue?vue&type=style&index=0&id=102f0ee6&lang=scss&scoped=true& ***!
  \*************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_style_index_0_id_102f0ee6_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/mini-css-extract-plugin/dist/loader.js??ref--8-oneOf-1-0!./node_modules/css-loader/dist/cjs.js??ref--8-oneOf-1-1!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/stylePostLoader.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-2!./node_modules/postcss-loader/src??ref--8-oneOf-1-3!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/sass-loader/dist/cjs.js??ref--8-oneOf-1-4!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-5!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./contract-sign.vue?vue&type=style&index=0&id=102f0ee6&lang=scss&scoped=true& */ 150);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_style_index_0_id_102f0ee6_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_style_index_0_id_102f0ee6_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_style_index_0_id_102f0ee6_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_style_index_0_id_102f0ee6_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__[key]; }) }(__WEBPACK_IMPORT_KEY__));
 /* harmony default export */ __webpack_exports__["default"] = (_A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_sign_vue_vue_type_style_index_0_id_102f0ee6_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 150:
/*!*****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/mini-css-extract-plugin/dist/loader.js??ref--8-oneOf-1-0!./node_modules/css-loader/dist/cjs.js??ref--8-oneOf-1-1!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/stylePostLoader.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-2!./node_modules/postcss-loader/src??ref--8-oneOf-1-3!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/sass-loader/dist/cjs.js??ref--8-oneOf-1-4!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-5!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-sign.vue?vue&type=style&index=0&id=102f0ee6&lang=scss&scoped=true& ***!
  \*****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

// extracted by mini-css-extract-plugin
    if(false) { var cssReload; }
  

/***/ })

},[[143,"common/runtime","common/vendor"]]]);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/user/contract-sign.js.map