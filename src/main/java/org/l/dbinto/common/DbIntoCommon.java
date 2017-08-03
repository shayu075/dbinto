package org.l.dbinto.common;

import org.l.dbinto.object.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Map;

import static org.l.dbinto.helper.JDBCHelper.dbInfo;
import static org.l.dbinto.helper.JDBCHelper.executeSql;
import static org.l.dbinto.helper.JDBCHelper.testConnection;

/**
 * Created by liao on 2017/8/1.
 */
@RestController
@RequestMapping("/bdInto")
public class DbIntoCommon {
    protected static Logger log = LoggerFactory.getLogger(DbIntoCommon.class);

    @RequestMapping("index")
    public String index(){
        DB db = new DB();
//        db.setUrl("jdbc:oracle:" + "thin:@192.168.2.98:1521:orcl");
//        db.setUser("KK");
//        db.setPassword("kk");
        db.setUrl("jdbc:mysql:" + "//192.168.2.178:3306/vehicle_db");
        db.setUser("root");
        db.setPassword("123456");
//        if(testConnection(db)){
//            return "成功";
//        }
//        return  "qqq";

        List re = dbInfo(db);
//        Map map = (Map) re.get(1);
        return re.toString();
    }

    @RequestMapping(value = "/users/{username}",method = RequestMethod.GET ,consumes="application/json")
    public String getUser(@PathVariable String username, ServletRequest request){
        Map map = request.getParameterMap();
        return "Welcome,"+username+ "     "+map.toString();
    }
}
