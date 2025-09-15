package cn.visolink.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 手动测试控制器
 */
@RestController
@RequestMapping("/test/manual")
public class ManualTestController {

    @Autowired
    private ShardingSphereTest shardingSphereTest;

    @GetMapping("/readwrite")
    public Map<String, Object> testReadWrite() {
        Map<String, Object> result = new HashMap<>();
        try {
            shardingSphereTest.testReadWriteSplitting();
            result.put("success", true);
            result.put("message", "读写分离测试完成，请查看控制台日志");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @GetMapping("/sharding")
    public Map<String, Object> testSharding() {
        Map<String, Object> result = new HashMap<>();
        try {
            shardingSphereTest.testSharding();
            result.put("success", true);
            result.put("message", "分表测试完成，请查看控制台日志");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @GetMapping("/all")
    public Map<String, Object> testAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            shardingSphereTest.runAllTests();
            result.put("success", true);
            result.put("message", "所有测试完成，请查看控制台日志");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
}
