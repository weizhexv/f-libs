package com.jkqj.common.utils;

import lombok.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 地址工具类
 *
 * @author cb
 * @date 2022-04-21
 */
public final class AddressUtils {

    private static final String regex = "(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
    private static final Pattern pattern = Pattern.compile(regex);

    public static Address cutAddress(String address) {
        if (address.startsWith("北京市") || address.startsWith("天津市") || address.startsWith("上海市") || address.startsWith("重庆市")) {
            address = address.substring(0, 3) + "市辖区" + address.substring(3);
        }

        Matcher m = pattern.matcher(address);
        String province = null, city = null, county = null, town = null, village = null;

        while (m.find()) {
            province = m.group("province");

            if (province.equals("北京市") || province.equals("天津市") || province.equals("上海市") || province.equals("重庆市")) {
                city = province;

                county = m.group("city");
                if (county.split("区").length > 1) {
                    town = county.substring(county.indexOf("区") + 1);
                    county = county.substring(0, county.indexOf("区") + 1);
                    if (town.contains("区")) {
                        town = town.substring(county.indexOf("区") + 1);
                    }
                } else {
                    county = m.group("county");
                    if (county.split("区").length > 1) {
                        town = county.substring(county.indexOf("区") + 1);
                        county = county.substring(0, county.indexOf("区") + 1);
                    }
                }
            } else {
                city = m.group("city");

                county = m.group("county");
                if (county != null && !"".equals(county)) {
                    if (county.split("市").length > 1 && county.indexOf("市") < 5) {
                        town = county;
                        county = county.substring(0, county.indexOf("市") + 1);
                        town = town.substring(county.indexOf("市") + 1);
                    }
                    if (county.split("旗").length > 1) {
                        town = county;
                        county = county.substring(0, county.indexOf("旗") + 1);
                        town = town.substring(county.indexOf("旗") + 1);
                    }
                    if (county.split("海域").length > 1) {
                        town = county;
                        county = county.substring(0, county.indexOf("海域") + 2);
                        town = town.substring(county.indexOf("海域") + 2);
                    }
                    if (county.split("区").length > 1) {
                        town = county;
                        county = county.substring(0, county.indexOf("区") + 1);
                        town = town.substring(county.indexOf("区") + 1);
                    }
                }
            }

            town += m.group("town");
            village = m.group("village");
        }

        return Address.builder()
                .province(province)
                .city(city)
                .county(county)
                .town(town)
                .village(village)
                .build();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        private String province;
        private String city;
        private String county;
        private String town;
        private String village;
    }

}
