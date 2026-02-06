(global["webpackJsonp"] = global["webpackJsonp"] || []).push([["pages/user/contract-edit"],{

/***/ 135:
/*!**********************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/main.js?{"page":"pages%2Fuser%2Fcontract-edit"} ***!
  \**********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(wx, createPage) {

var _interopRequireDefault = __webpack_require__(/*! @babel/runtime/helpers/interopRequireDefault */ 4);
__webpack_require__(/*! uni-pages */ 26);
var _vue = _interopRequireDefault(__webpack_require__(/*! vue */ 25));
var _contractEdit = _interopRequireDefault(__webpack_require__(/*! ./pages/user/contract-edit.vue */ 136));
// @ts-ignore
wx.__webpack_require_UNI_MP_PLUGIN__ = __webpack_require__;
createPage(_contractEdit.default);
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/wx.js */ 1)["default"], __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/index.js */ 2)["createPage"]))

/***/ }),

/***/ 136:
/*!***************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-edit.vue ***!
  \***************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _contract_edit_vue_vue_type_template_id_44d71a5a_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./contract-edit.vue?vue&type=template&id=44d71a5a&scoped=true& */ 137);
/* harmony import */ var _contract_edit_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./contract-edit.vue?vue&type=script&lang=js& */ 139);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _contract_edit_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _contract_edit_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__[key]; }) }(__WEBPACK_IMPORT_KEY__));
/* harmony import */ var _contract_edit_vue_vue_type_style_index_0_id_44d71a5a_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./contract-edit.vue?vue&type=style&index=0&id=44d71a5a&lang=scss&scoped=true& */ 141);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/runtime/componentNormalizer.js */ 32);

var renderjs





/* normalize component */

var component = Object(_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_3__["default"])(
  _contract_edit_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__["default"],
  _contract_edit_vue_vue_type_template_id_44d71a5a_scoped_true___WEBPACK_IMPORTED_MODULE_0__["render"],
  _contract_edit_vue_vue_type_template_id_44d71a5a_scoped_true___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"],
  false,
  null,
  "44d71a5a",
  null,
  false,
  _contract_edit_vue_vue_type_template_id_44d71a5a_scoped_true___WEBPACK_IMPORTED_MODULE_0__["components"],
  renderjs
)

component.options.__file = "pages/user/contract-edit.vue"
/* harmony default export */ __webpack_exports__["default"] = (component.exports);

/***/ }),

/***/ 137:
/*!**********************************************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-edit.vue?vue&type=template&id=44d71a5a&scoped=true& ***!
  \**********************************************************************************************************************************/
/*! exports provided: render, staticRenderFns, recyclableRender, components */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_template_id_44d71a5a_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--17-0!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/template.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-uni-app-loader/page-meta.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./contract-edit.vue?vue&type=template&id=44d71a5a&scoped=true& */ 138);
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "render", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_template_id_44d71a5a_scoped_true___WEBPACK_IMPORTED_MODULE_0__["render"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_template_id_44d71a5a_scoped_true___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "recyclableRender", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_template_id_44d71a5a_scoped_true___WEBPACK_IMPORTED_MODULE_0__["recyclableRender"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "components", function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_template_id_44d71a5a_scoped_true___WEBPACK_IMPORTED_MODULE_0__["components"]; });



/***/ }),

/***/ 138:
/*!**********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--17-0!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/template.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-uni-app-loader/page-meta.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-edit.vue?vue&type=template&id=44d71a5a&scoped=true& ***!
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
  var g0 = _vm.currentStep === 1 ? _vm.form.projectDesc.length : null
  var g1 = _vm.currentStep === 1 ? _vm.form.techRequirements.length : null
  var g2 = _vm.currentStep === 1 ? _vm.form.deliveryStandard.length : null
  var g3 =
    _vm.currentStep === 2 && _vm.order.totalAmount
      ? _vm.order.totalAmount.toLocaleString()
      : null
  var l0 =
    _vm.currentStep === 2
      ? _vm.__map(_vm.stages, function (stage, index) {
          var $orig = _vm.__get_orig(stage)
          var g4 = stage.amount ? stage.amount.toLocaleString() : null
          return {
            $orig: $orig,
            g4: g4,
          }
        })
      : null
  _vm.$mp.data = Object.assign(
    {},
    {
      $root: {
        g0: g0,
        g1: g1,
        g2: g2,
        g3: g3,
        l0: l0,
      },
    }
  )
}
var recyclableRender = false
var staticRenderFns = []
render._withStripped = true



/***/ }),

/***/ 139:
/*!****************************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-edit.vue?vue&type=script&lang=js& ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/babel-loader/lib!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--13-1!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/script.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./contract-edit.vue?vue&type=script&lang=js& */ 140);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__[key]; }) }(__WEBPACK_IMPORT_KEY__));
 /* harmony default export */ __webpack_exports__["default"] = (_A_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 140:
/*!***********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/babel-loader/lib!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--13-1!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/script.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-edit.vue?vue&type=script&lang=js& ***!
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
var _defineProperty2 = _interopRequireDefault(__webpack_require__(/*! @babel/runtime/helpers/defineProperty */ 11));
var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__(/*! @babel/runtime/helpers/asyncToGenerator */ 41));
var _request = __webpack_require__(/*! @/utils/request */ 42);
function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); enumerableOnly && (symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; })), keys.push.apply(keys, symbols); } return keys; }
function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = null != arguments[i] ? arguments[i] : {}; i % 2 ? ownKeys(Object(source), !0).forEach(function (key) { (0, _defineProperty2.default)(target, key, source[key]); }) : Object.getOwnPropertyDescriptors ? Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)) : ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } return target; }
var _default = {
  data: function data() {
    return {
      orderId: null,
      currentStep: 1,
      order: {},
      stages: [],
      form: {
        title: '',
        projectDesc: '',
        techRequirements: '',
        deliveryStandard: '',
        deliveryDeadline: '',
        breachClause: '',
        confidentialClause: '',
        ipClause: '',
        disputeClause: ''
      },
      expandedClauses: {
        breach: true,
        confidential: false,
        ip: false,
        dispute: false
      },
      loading: false
    };
  },
  onLoad: function onLoad(options) {
    if (options.orderId) {
      this.orderId = options.orderId;
      this.loadData();
    } else {
      uni.showToast({
        title: '订单ID缺失',
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
        var orderRes, contractRes;
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
                  url: "/order/".concat(_this.orderId),
                  method: 'GET'
                });
              case 4:
                orderRes = _context.sent;
                if (orderRes.data) {
                  _this.order = orderRes.data.order || orderRes.data;
                  _this.stages = orderRes.data.stages || [];

                  // 初始化表单
                  _this.form.title = _this.order.title + ' - 项目合同';
                  _this.form.projectDesc = _this.order.description || '';
                  _this.form.techRequirements = _this.order.techStack || '';
                  _this.form.deliveryDeadline = _this.order.deliveryTime ? _this.formatDate(_this.order.deliveryTime) : '';
                }

                // 尝试加载已有合同
                _context.prev = 6;
                _context.next = 9;
                return (0, _request.request)({
                  url: "/contract/order/".concat(_this.orderId),
                  method: 'GET'
                });
              case 9:
                contractRes = _context.sent;
                if (contractRes.data) {
                  // 填充已有合同数据
                  _this.form = {
                    title: contractRes.data.title || _this.form.title,
                    projectDesc: contractRes.data.projectDesc || _this.form.projectDesc,
                    techRequirements: contractRes.data.techRequirements || _this.form.techRequirements,
                    deliveryStandard: contractRes.data.deliveryStandard || '',
                    deliveryDeadline: contractRes.data.deliveryDeadline ? _this.formatDate(contractRes.data.deliveryDeadline) : _this.form.deliveryDeadline,
                    breachClause: contractRes.data.breachClause || _this.getDefaultClause('breach'),
                    confidentialClause: contractRes.data.confidentialClause || _this.getDefaultClause('confidential'),
                    ipClause: contractRes.data.ipClause || _this.getDefaultClause('ip'),
                    disputeClause: contractRes.data.disputeClause || _this.getDefaultClause('dispute')
                  };
                } else {
                  // 使用默认条款
                  _this.initDefaultClauses();
                }
                _context.next = 16;
                break;
              case 13:
                _context.prev = 13;
                _context.t0 = _context["catch"](6);
                // 没有合同，使用默认条款
                _this.initDefaultClauses();
              case 16:
                _context.next = 22;
                break;
              case 18:
                _context.prev = 18;
                _context.t1 = _context["catch"](1);
                console.error('加载数据失败', _context.t1);
                uni.showToast({
                  title: '加载失败',
                  icon: 'none'
                });
              case 22:
                _context.prev = 22;
                uni.hideLoading();
                return _context.finish(22);
              case 25:
              case "end":
                return _context.stop();
            }
          }
        }, _callee, null, [[1, 18, 22, 25], [6, 13]]);
      }))();
    },
    initDefaultClauses: function initDefaultClauses() {
      this.form.breachClause = this.getDefaultClause('breach');
      this.form.confidentialClause = this.getDefaultClause('confidential');
      this.form.ipClause = this.getDefaultClause('ip');
      this.form.disputeClause = this.getDefaultClause('dispute');
    },
    getDefaultClause: function getDefaultClause(type) {
      var clauses = {
        breach: '1. 甲方逾期付款超过3日，需支付应付款项0.1%/日的违约金\n2. 乙方逾期交付超过7日，甲方有权解除合同并要求退款\n3. 任一方违约导致合同解除，违约方承担对方实际损失',
        confidential: '1. 双方对项目相关技术资料、商业信息负有保密义务\n2. 保密期限为合同终止后2年\n3. 违反保密义务需承担违约责任',
        ip: '1. 乙方交付成果的知识产权归甲方所有\n2. 乙方保证交付物不侵犯第三方知识产权\n3. 如涉及第三方开源组件，乙方需提前说明',
        dispute: '1. 本合同争议首先通过平台调解解决\n2. 调解不成的，任一方可向合同签订地人民法院提起诉讼'
      };
      return clauses[type] || '';
    },
    formatDate: function formatDate(dateStr) {
      if (!dateStr) return '';
      var date = new Date(dateStr);
      var year = date.getFullYear();
      var month = String(date.getMonth() + 1).padStart(2, '0');
      var day = String(date.getDate()).padStart(2, '0');
      return "".concat(year, "-").concat(month, "-").concat(day);
    },
    onDateChange: function onDateChange(e) {
      this.form.deliveryDeadline = e.detail.value;
    },
    toggleClause: function toggleClause(type) {
      this.expandedClauses[type] = !this.expandedClauses[type];
    },
    nextStep: function nextStep() {
      // 验证当前步骤
      if (this.currentStep === 1) {
        if (!this.form.title) {
          uni.showToast({
            title: '请填写合同标题',
            icon: 'none'
          });
          return;
        }
        if (!this.form.projectDesc) {
          uni.showToast({
            title: '请填写项目描述',
            icon: 'none'
          });
          return;
        }
        if (!this.form.deliveryStandard) {
          uni.showToast({
            title: '请填写验收标准',
            icon: 'none'
          });
          return;
        }
      }
      if (this.currentStep < 3) {
        this.currentStep++;
      }
    },
    prevStep: function prevStep() {
      if (this.currentStep > 1) {
        this.currentStep--;
      }
    },
    saveDraft: function saveDraft() {
      var _this2 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee2() {
        return _regenerator.default.wrap(function _callee2$(_context2) {
          while (1) {
            switch (_context2.prev = _context2.next) {
              case 0:
                uni.showLoading({
                  title: '保存中...'
                });
                _context2.prev = 1;
                _context2.next = 4;
                return (0, _request.request)({
                  url: '/contract/create',
                  method: 'POST',
                  data: _objectSpread({
                    orderId: _this2.orderId
                  }, _this2.form)
                });
              case 4:
                uni.hideLoading();
                uni.showToast({
                  title: '保存成功',
                  icon: 'success'
                });
                _context2.next = 12;
                break;
              case 8:
                _context2.prev = 8;
                _context2.t0 = _context2["catch"](1);
                uni.hideLoading();
                uni.showToast({
                  title: _context2.t0.message || '保存失败',
                  icon: 'none'
                });
              case 12:
              case "end":
                return _context2.stop();
            }
          }
        }, _callee2, null, [[1, 8]]);
      }))();
    },
    submitContract: function submitContract() {
      var _this3 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee3() {
        var _saveRes$data, saveRes, contractId, contractRes;
        return _regenerator.default.wrap(function _callee3$(_context3) {
          while (1) {
            switch (_context3.prev = _context3.next) {
              case 0:
                if (!(!_this3.form.title || !_this3.form.projectDesc || !_this3.form.deliveryStandard)) {
                  _context3.next = 3;
                  break;
                }
                uni.showToast({
                  title: '请完善必填信息',
                  icon: 'none'
                });
                return _context3.abrupt("return");
              case 3:
                uni.showLoading({
                  title: '提交中...'
                });
                _context3.prev = 4;
                _context3.next = 7;
                return (0, _request.request)({
                  url: '/contract/create',
                  method: 'POST',
                  data: _objectSpread({
                    orderId: _this3.orderId
                  }, _this3.form)
                });
              case 7:
                saveRes = _context3.sent;
                contractId = (_saveRes$data = saveRes.data) === null || _saveRes$data === void 0 ? void 0 : _saveRes$data.id;
                if (contractId) {
                  _context3.next = 11;
                  break;
                }
                throw new Error('合同保存失败');
              case 11:
                _context3.next = 13;
                return (0, _request.request)({
                  url: "/contract/".concat(contractId),
                  method: 'GET'
                });
              case 13:
                contractRes = _context3.sent;
                if (!(contractRes.data && contractRes.data.status !== 0)) {
                  _context3.next = 18;
                  break;
                }
                // 合同已提交，直接跳转签署页面
                uni.hideLoading();
                uni.showModal({
                  title: '提示',
                  content: '合同已提交，请进行签署',
                  showCancel: false,
                  success: function success() {
                    uni.redirectTo({
                      url: "/pages/user/contract-sign?orderId=".concat(_this3.orderId, "&contractId=").concat(contractId)
                    });
                  }
                });
                return _context3.abrupt("return");
              case 18:
                _context3.next = 20;
                return (0, _request.request)({
                  url: "/contract/".concat(contractId, "/submit"),
                  method: 'POST'
                });
              case 20:
                uni.hideLoading();

                // 提示并跳转到签署页面
                uni.showModal({
                  title: '提交成功',
                  content: '合同已提交，请进行签署',
                  showCancel: false,
                  success: function success() {
                    uni.redirectTo({
                      url: "/pages/user/contract-sign?orderId=".concat(_this3.orderId, "&contractId=").concat(contractId)
                    });
                  }
                });
                _context3.next = 28;
                break;
              case 24:
                _context3.prev = 24;
                _context3.t0 = _context3["catch"](4);
                uni.hideLoading();
                uni.showToast({
                  title: _context3.t0.message || '提交失败',
                  icon: 'none'
                });
              case 28:
              case "end":
                return _context3.stop();
            }
          }
        }, _callee3, null, [[4, 24]]);
      }))();
    }
  }
};
exports.default = _default;
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/index.js */ 2)["default"]))

/***/ }),

/***/ 141:
/*!*************************************************************************************************************************************************!*\
  !*** R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-edit.vue?vue&type=style&index=0&id=44d71a5a&lang=scss&scoped=true& ***!
  \*************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_style_index_0_id_44d71a5a_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/mini-css-extract-plugin/dist/loader.js??ref--8-oneOf-1-0!./node_modules/css-loader/dist/cjs.js??ref--8-oneOf-1-1!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/stylePostLoader.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-2!./node_modules/postcss-loader/src??ref--8-oneOf-1-3!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/sass-loader/dist/cjs.js??ref--8-oneOf-1-4!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-5!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./contract-edit.vue?vue&type=style&index=0&id=44d71a5a&lang=scss&scoped=true& */ 142);
/* harmony import */ var _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_style_index_0_id_44d71a5a_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_style_index_0_id_44d71a5a_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_style_index_0_id_44d71a5a_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_style_index_0_id_44d71a5a_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__[key]; }) }(__WEBPACK_IMPORT_KEY__));
 /* harmony default export */ __webpack_exports__["default"] = (_A_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_oneOf_1_0_A_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_8_oneOf_1_1_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_2_A_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_8_oneOf_1_3_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_sass_loader_dist_cjs_js_ref_8_oneOf_1_4_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_8_oneOf_1_5_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_A_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_contract_edit_vue_vue_type_style_index_0_id_44d71a5a_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 142:
/*!*****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/mini-css-extract-plugin/dist/loader.js??ref--8-oneOf-1-0!./node_modules/css-loader/dist/cjs.js??ref--8-oneOf-1-1!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/stylePostLoader.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-2!./node_modules/postcss-loader/src??ref--8-oneOf-1-3!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/sass-loader/dist/cjs.js??ref--8-oneOf-1-4!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--8-oneOf-1-5!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!R:/Code_Repository/TrustFulfillment/fronted_uniapp/pages/user/contract-edit.vue?vue&type=style&index=0&id=44d71a5a&lang=scss&scoped=true& ***!
  \*****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

// extracted by mini-css-extract-plugin
    if(false) { var cssReload; }
  

/***/ })

},[[135,"common/runtime","common/vendor"]]]);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/user/contract-edit.js.map