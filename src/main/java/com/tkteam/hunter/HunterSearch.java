package com.tkteam.hunter;


import com.tkteam.utils.HttpTool;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;

public class HunterSearch {
    private final HashMap<String,String> headers=new HashMap<>();

    public String getResult(String key,String grammar,String isweb,int page,String code,String starttime,String endtime) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        String search_url = "https://hunter.qianxin.com/openApi/search?api-key="+key+"&search="+grammar+"&page="+page+"&page_size=100&is_web="+isweb+"&status_code="+code+"&start_time="+"%22"+starttime+"%22"+"&end_time="+"%22"+endtime+"%22";
        return HttpTool.get(search_url,this.headers).getText();       //返回获取JSON结果
    }

}

