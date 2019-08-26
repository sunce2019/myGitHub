package com.pay.payAssist;

/**
 * 配置信息
 */
public class PayConfig {

    public final static String HOST_URL = "http://subapi.test.fzwxy8.com";
    public final static String UNIFIED_PAY = "/api/pay/unifiedPay";//统一下单接口
    public final static String QUERY_ORDER = "/api/pay/queryOrder";//查询接口

    //平台公钥
    public final static String SUB_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNMTlMFeUn5LOKqbs2rMP6mxNyZwgvyrkknfdg1yfD1QLlp2BaYetmuDaeyfEDLTmz49sxNC1GqiuBCLTrvshQIshTjA1wekFpFozFozosy9/BCigMd4BYrnnymmW4JwY3/Oz5zN56izN6AqVOUwMLFW0LiihtrlkTu/CVJ2pl3wIDAQAB";

    //商户公钥
    public final static String MERCHANT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChJ6JTT/yMoKoUmHGBssGboQIs17miDaqAgXx92mfYe+gR3ustg3uYwtqUERLc1bM6A5AS2f30/KrSJAURTCEkjiNHK6wGZToaE7dqo7bmQeodqyUuf/EwC9ttAv4pjOMBIuNDF4A99p0Zg03ZRtKXvi7kZOPOhbTbAO2+u50EEwIDAQAB";

    //商户私钥
    public final static String MERCHANT_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKEnolNP/IygqhSYcYGywZuhAizXuaINqoCBfH3aZ9h76BHe6y2De5jC2pQREtzVszoDkBLZ/fT8qtIkBRFMISSOI0crrAZlOhoTt2qjtuZB6h2rJS5/8TAL220C/imM4wEi40MXgD32nRmDTdlG0pe+LuRk486FtNsA7b67nQQTAgMBAAECgYBZn3AgAyjBkIxl/c2KQr53bZiHFcXwAMekxd5VI4SDGY2beqyx2CCBeUQT+lbSJ6qQBfE2+rLIb0gUq+GBmCQJjRH9ekxV/8FKR+g82krYTB4yJGp25aj2pE4WKYAlWB9Zu8Ju0xwkVIH1TbhGCv9hR289sR+7YJHrLJHgo9hNYQJBANdAKgNlbku99f9icDU1G7luv5BbFiPqdYtcE7cTBRVe9FzFCMJ9yvs87optMNfEJFppZAnr2P9cT0rNT3J6mlECQQC/qcm7gxMoWHAulWj/hkZ4Vmfw2j2rZ4I5fqoKBnIHpfnxKpfEZQ/DyySlWjN9tJBg7+zCdnaGCOWD+5QvJXsjAkA9TOCqJvDKUUcFfBtIi99hZ9DWY5pikBr5nTdgI+KGu4wL/3EHMpvkAO5OLdqDGDDiLrF2wdwCnV9XhjDiAViBAkBOr8xMmYGR/M0BswGKdnwUB5Vgwpiy+Cn0pNX2i7LhBPlaU5w/WyZfwdTnteKR50GnKplPNQdv+9MOnoi12nN1AkAdUIogBcQXNZoUEIupQx0Ugr+smwHIWqV/qJdalZVmJRwIlcdNiAG093tquk7/nUEJdv7aHa7L/IPgmfF8ALNH";
    //商户号
    public final static String MERCHANT_NUMBER = "A02000013000025";

    //retCode
    public final static String TWO_HUNDRED = "200";

}
