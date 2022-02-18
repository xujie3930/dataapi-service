package com.jinninghui.datasphere.icreditstudio.framework.utils;

import java.util.HashMap;
import java.util.Map;

public interface Constants {
	public final static String SUCCESS = ReturnCode.SUCCESS.code;
	public final static String FAIL = ReturnCode.FAIL.code;
	public final static String INVALID_PARAM = ReturnCode.INVALID_PARAM.code;

	enum ReturnCode {
		SUCCESS("0", "处理成功"),
		FAIL("1", "系统内部错误"),
		INVALID_PARAM("2", "参数错误"),
		REMOTE_ERROR("3", "远程调用失败"),
		DB_ERROR("4", "数据库操作失败"),
		CACHE_ERROR("5", "缓存操作失败"),
		MQ_ERROR("6", "消息调用失败"),
		FILE_ERROR("7", "读写文件失败"),
		UNSUPPORT_MQ_MSG_TYPE("8", "不支持的消息类型"),
		REPEATED_REQUEST("0103", "重复请求");

		public final String code;
		public final String comment;

		ReturnCode(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	enum HeadKey {
		TOKEN("x-token", "令牌"),
		USERID("x-userid", "用户ID"),
		CUSTOMER_CODE("x-customer-code", "客户编码"),
		USERTYPE("x-user-type", "用户类型，参考Constants.UserType定义"),
		BUSINESS_TYPE("x-business-type", "业务类型"),
		EXTRA("x-extra", "扩展信息，在令牌中保存，不做其他处理"),
		IP("x-user-ip", "用户IP"),
		UA("x-user-ua", "用户UA");

		public final String code;
		public final String comment;

		HeadKey(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	enum ActionType {
		COMMAND("CMD", "命令型，Method只返回String，表示成败"),
		QUERY("QRY", "查询型，Method返回值对象或者值对象集合");

		public final String code;
		public final String comment;

		ActionType(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	public enum PayState {

		SUCCESS("SUCCESS", "成功"),
		FAIL("FAIL", "失败"),
		INIT("INIT", "初始化"),
		PROCESSING("IN_PROCESS", "处理中");

		public final String code;
		public final String message;

		PayState(String code, String message) {
			this.code = code;
			this.message = message;
		}

		private static final Map<String, PayState> CODE_MAP = new HashMap<String, PayState>();

		static {
			for (PayState typeEnum : PayState.values()) {
				CODE_MAP.put(typeEnum.code, typeEnum);
			}
		}

		public static PayState getEnum(String typeName) {
			return CODE_MAP.get(typeName);
		}
	}

	/**
	 * 费率模式
	 * 
	 * @author Administrator
	 *
	 */
	enum RateMode {
		FIX_RATE((short) 1, "固定费率"),
		PERCENT_RATE((short) 2, "百分比费率");
		public final Short code;
		public final String comment;

		RateMode(Short code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	/**
	 * 系统预置的业务编码
	 * 
	 * @author YangMeng
	 *
	 */
	enum BusinessCode {

		YMF("YMF", "一码付业务"),
		API_REFUND("API_REFUND", "API退款业务"),
		FZ("FZ", "分账，将某条支付订单的金额分给其他客户"),
		BFZ("BFZ", "被分账，从其他客户的支付订单分得金额"),
		WITHDRAW("WITHDRAW", "提现业务"),
		DL("DL", "代理业务"),
		TK("TK", "退款业务");

		public final String code;
		public final String comment;

		BusinessCode(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}

		private static final Map<String, BusinessCode> CODE_MAP = new HashMap<String, BusinessCode>();

		static {
			for (BusinessCode typeEnum : BusinessCode.values()) {
				CODE_MAP.put(typeEnum.code, typeEnum);
			}
		}

		public static BusinessCode getEnum(String code) {
			return CODE_MAP.get(code);
		}
	}

	/**
	 * 业务参数，这里还是需要的，否则一些业务逻辑的判断就不好做了
	 * 
	 * @author lidab
	 */

	enum BusinessParamCode {

		AGENT_CUSTOMER_CODE("agentCustomerCode", "代理商客户编码"),
		MAX_TXS_AMOUNT("maxTxsAmount", "最大交易限额"),
		MIN_TXS_AMOUNT("minTxsAmount", "最小交易限额"),
		/**
		 * 分账客户的客户编码，只允许该商户给对应的子商户分账
		 */
		FZ_CUSTOMER_CODE("fzCustomerCode", "分账客户编码"),
		SUBSCRIPTION_RATIO("subscriptionRatio", "兑换比例");
		public final String code;
		public final String comment;

		BusinessParamCode(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}

	}

	/**
	 * 业务类别
	 */
	enum BusinessCategory {
		DSP_BASIC_PAY_SERVICE("dspBasicPayService", "基础支付类业务"),
		DSP_FZ_SERVICE("dspFzService", "分账类业务(分账)"),
		DSP_BFZ_SERVICE("dspBfzService", "分账类业务(被分账)"),
		DSP_FZ_PAY_SERVICE("dspFzPayService", "分账支付类业务"),
		DSP_ACCOUNT_SERVICE("dspAccountService", "账务服务"),
		DSP_SPECIAL_SERVICE("dspSpecialService", "系统专用业务"),
		DSP_MEMBER_RECHARGE_SERVICE("dspMemberRechargeService", "充值服务");
		public final String code;
		public final String comment;

		BusinessCategory(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	/**
	 * 基础支付类业务
	 */
	enum BasicPayService {
		YMF("YMF", "一码付"),
		AlIJSAPI("AliJSAPI", "支付宝生活号"),
		AlIMICRO("AliMicro", "支付宝被扫"),
		WXMICRO("WxMicro", "微信被扫"),
		AlINATIVE("AliNative", "支付宝主扫"),
		QUICKPAY("QuickPay", "快捷支付"),
		WXMWEB("WxMWEB", "微信H5支付"),
		WXJSAPI("WxJSAPI", "微信公众号支付"),
		WXNATIVE("WxNative", "微信主扫"),
		SAVINGCARDPAY("SavingCardPay", "网银储蓄卡"),
		CREDITCARDPAY("CreditCardPay", "网银信用卡"),
		REFUND_USING_FLOAT("RefundUsingFloat", "支付_在途金额退款");
		public final String code;
		public final String comment;

		BasicPayService(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	/**
	 * 分账类业务(分账) 注：记账计入簿记账户
	 */
	enum FzService {
		FZ("FZ", "分账"),
		FZ_REFUND_USING_FLOAT("FzRefundUsingFloat", "分账_在途金额退款");
		public final String code;
		public final String comment;

		FzService(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	/**
	 * 分账类业务(被分账) 注：记账计入分账账户
	 */
	enum BfzService {
		BFZ("BFZ", "被分账"),
		BFZ_REFUND_USING_FLOAT("BfzRefundUsingFloat", "被分账_在途金额退款");
		public final String code;
		public final String comment;

		BfzService(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	/**
	 * 分账支付类业务
	 */
	enum FzPayService {
		FZ_ALIJSAPI("FZ-AliJSAPI", "分账-支付宝生活号"),
		FZ_ALIMICRO("FZ-AliMicro", "分账-支付宝被扫"),
		FZ_WXMICRO("FZ-WxMicro", "分账-微信被扫"),
		FZ_ALINATIVE("FZ-AliNative", "分账-支付宝主扫"),
		FZ_QUICKPAY("FZ-QuickPay", "分账-快捷支付"),
		FZ_WXMWEB("FZ-WxMWEB", "分账-微信H5支付"),
		FZ_WXJSAPI("FZ-WxJSAPI", "分账-微信公众号支付"),
		FZ_WXNATVIE("FZ-WxNative", "分账-微信主扫"),
		FZ_SAVINGARDPAY("FZ-SavingCardPay", "分账网银支付"),
		FZ_ENTERPRISEUNION("FZ-SavingCardPay", "分账网银支付");
		public final String code;
		public final String comment;

		FzPayService(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	/**
	 * DSP账户服务(实时结算)
	 */
	enum AccountService {
		REFUND_USING_AVAILABLE("RefundUsingAvailable", "支付_可用金额退款"),
		FZ_REFUND_USING_AVAILABLE("FzRefundUsingAvailable", "分账_可用金额退款"),
		BFZ_REFUND_USING_AVAILABLE("BfzRefundUsingAvailable", "被分账_可用金额退款"),
		FZ_PAY_REFUND_USING_AVAILABLE("FzPayRefundUsingAvailable", "分账支付_可用金额退款"),
		WITHDRAW("Withdraw", "代付"),
		DL("DL", "代理"),
		RECHARGE("Recharge", "充值"),
		MEMBER_INSIDE_PAY("MemberInsidePay", "会员内转"),
		MEMBER_EXCHANGE_IN("MemberExchangeIn", "会员兑换转入业务"),
		MEMBER_EXCHANGE_OUT("MemberExchangeOut", "会员兑换转出业务"),
		MEMBER_OPPOSITE_EXCHANGE_IN("MemberOppositeExchangeIn", "会员反兑换转入业务"),
		MEMBER_OPPOSITE_EXCHANGE_OUT("MemberOppositeExchangeOut", "会员反兑换转出业务"),
		MEMBER_INSIDE_PAY_CENT("MemberInsidePayCent", "会员内转分成业务"),
		D0_QUICKPAY("D0-QuickPay", "D0快捷支付"),
		D0_WITHDRAW_AUTO("D0-Withdraw-Auto", "D0代付-自动");

		public final String code;
		public final String comment;

		AccountService(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	/**
	 * DSP账户服务
	 */
	enum MemberRechargeService {
		MEMBER_RECHARGE_ALIJSAPI("MemberRecharge-AliJSAPI", "会员充值-支付宝生活号"),
		MEMBER_RECHARGE_ALIMICRO("MemberRecharge-AliMicro", "会员充值-支付宝被扫"),
		MEMBER_RECHARGE_WXMICRO("MemberRecharge-WxMicro", "会员充值-微信被扫"),
		MEMBER_RECHARGE_ALINATIVE("MemberRecharge-AliNative", "会员充值-支付宝主扫"),
		MEMBER_RECHARGE_QUICKPAY("MemberRecharge-QuickPay", "会员充值-快捷支付"),
		MEMBER_RECHARGE_WXMWEB("MemberRecharge-WxMWEB", "会员充值-微信H5支付"),
		MEMBER_RECHARGE_WXJSAPI("MemberRecharge-WxJSAPI", "会员充值-企业网银"),
		MEMBER_RECHARGE_WXNATIVE("MemberRecharge-WxNative", "会员充值-微信主扫"),
		MEMBER_RECHARGE_UNION("MemberRecharge-Union", "会员充值-企业网银");
		public final String code;
		public final String comment;

		MemberRechargeService(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	/**
	 * 系统专用业务
	 */
	enum SpecialService {
		FR("FR", "分润");
		public final String code;
		public final String comment;

		SpecialService(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}
	}

	/**
	 * 结算粒度
	 *
	 */
	enum settGrained {

		BUSINESS_CATEGORY("BusinessCategory", "业务类型"),
		BUSINESS("business", "业务");

		public final String code;
		public final String comment;

		settGrained(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}

	}

	enum CustomerCategory {

		CUSTOMER("CUSTOMER", "客户"), /* 要营业执照或身份证全局唯一的类型 */
		CUSTOMER_MEMBER("CUSTOMER_MEMBER", "客户(的)会员");/* 营业执照或身份证只需要在所属客户下唯一即可 */

		public final String code;
		public final String comment;

		CustomerCategory(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}

	}

}
