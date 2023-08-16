package com.jkqj.dubbo.generic.helper;


import com.jkqj.dubbo.generic.GenericCallContext;
import com.jkqj.dubbo.generic.http.GenericCallMapping;
import org.apache.commons.lang3.StringUtils;

public class MappingLineParser {
    private MappingLineParser() {
    }

    public static GenericCallMapping parseLine(String line) {
        String[] items = StringUtils.split(line, ':');
       GenericCallMapping mapping = new GenericCallMapping();
        mapping.setHtpPath(items[0]);

        GenericCallContext callContext = new GenericCallContext();
        mapping.setCallContext(callContext);

        String[] callInfos = StringUtils.split(items[1], '/');

        callContext.setGroup(callInfos[0]);
        callContext.setInterfaceName(callInfos[1]);
        callContext.setMethod(callInfos[2]);
        callContext.setParameterClassNames(new String[]{callInfos[3]});
        callContext.setRetries(Integer.parseInt(callInfos[4]));
        if (callInfos.length == 6) {
            callContext.setVersion(callInfos[5]);
        }
        return mapping;
    }
}
