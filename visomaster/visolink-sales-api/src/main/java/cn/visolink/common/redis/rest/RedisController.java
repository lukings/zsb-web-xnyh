package cn.visolink.common.redis.rest;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.common.monitor.domain.vo.RedisVo;
import cn.visolink.common.redis.model.DeleteUserRedisKeyRequest;
import cn.visolink.common.redis.model.DeleteUserRedisKeyResponse;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author WCL
 * @date 2018-12-10
 */
@RestController
@RequestMapping("api")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @Log("查询Redis缓存")
    @GetMapping(value = "/redis")
    public ResponseEntity getRedis(String key, Pageable pageable){
//        String username = SecurityUtils.getUsername();
        return new ResponseEntity(redisService.findByKey(key,pageable), HttpStatus.OK);
    }

    @Log("删除Redis缓存")
    @DeleteMapping(value = "/redis")
    public ResponseEntity delete(@RequestBody RedisVo resources){
        redisService.delete(resources.getKey());
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("删除用户Redis键")
    @ApiOperation(value = "删除用户Redis键", notes = "根据用户账户列表批量删除对应的Redis键值")
    @PostMapping(value = "/redis/deleteUserKeys")
    public ResultBody<DeleteUserRedisKeyResponse> deleteUserRedisKeys(@RequestBody DeleteUserRedisKeyRequest request) {
        try {
            // 参数校验
            if (request.getUserAccounts() == null || request.getUserAccounts().isEmpty()) {
                return ResultBody.error(-1000_01, "用户账户列表不能为空");
            }

            DeleteUserRedisKeyResponse response = new DeleteUserRedisKeyResponse();
            response.setUserAccounts(request.getUserAccounts());
            
            List<String> allDeletedKeys = new ArrayList<>();
            Map<String, DeleteUserRedisKeyResponse.UserDeleteResult> userResults = new HashMap<>();
            int totalDeletedCount = 0;
            int successUserCount = 0;
            int failedUserCount = 0;
            
            // 遍历每个用户账户
            for (String userAccount : request.getUserAccounts()) {
                if (userAccount == null || userAccount.trim().isEmpty()) {
                    continue;
                }
                
                DeleteUserRedisKeyResponse.UserDeleteResult userResult = new DeleteUserRedisKeyResponse.UserDeleteResult();
                List<String> userDeletedKeys = new ArrayList<>();
                int userDeletedCount = 0;
                
                try {
                    String trimmedUserAccount = userAccount.trim();
                    
                    // 构建要删除的键模式
                    Set<String> keyPatterns = new HashSet<>();
                    if (Boolean.TRUE.equals(request.getDeleteAppKeys())) {
                        keyPatterns.add("viso.VISO.User.info." + trimmedUserAccount);
                        keyPatterns.add("TokenKey." + trimmedUserAccount);
                    }
                    if (Boolean.TRUE.equals(request.getDeletePcKeys())) {
                        keyPatterns.add("viso.VISO.User.info." + trimmedUserAccount);
                        keyPatterns.add("TokenKey." + trimmedUserAccount);
                    }
                    
                    // 执行删除操作
                    for (String keyPattern : keyPatterns) {
                        try {
                            // 使用现有的RedisService删除方法
                            redisService.delete(keyPattern);
                            userDeletedKeys.add(keyPattern);
                            userDeletedCount++;
                        } catch (Exception e) {
                            // 记录删除失败的键，但不中断整个流程
                            System.err.println("删除键失败: " + keyPattern + ", 错误: " + e.getMessage());
                        }
                    }
                    
                    userResult.setSuccess(true);
                    userResult.setMessage("删除操作完成");
                    userResult.setDeletedKeys(userDeletedKeys);
                    userResult.setDeletedCount(userDeletedCount);
                    
                    allDeletedKeys.addAll(userDeletedKeys);
                    totalDeletedCount += userDeletedCount;
                    successUserCount++;
                    
                } catch (Exception e) {
                    userResult.setSuccess(false);
                    userResult.setMessage("删除失败：" + e.getMessage());
                    userResult.setDeletedKeys(new ArrayList<>());
                    userResult.setDeletedCount(0);
                    failedUserCount++;
                }
                
                userResults.put(userAccount, userResult);
            }
            
            response.setSuccess(successUserCount > 0);
            response.setMessage(String.format("批量删除操作完成，成功：%d个用户，失败：%d个用户", successUserCount, failedUserCount));
            response.setDeletedKeys(allDeletedKeys);
            response.setDeletedCount(totalDeletedCount);
            response.setUserResults(userResults);
            response.setSuccessUserCount(successUserCount);
            response.setFailedUserCount(failedUserCount);
            
            return ResultBody.success(response);
            
        } catch (Exception e) {
            DeleteUserRedisKeyResponse errorResponse = new DeleteUserRedisKeyResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("删除失败：" + e.getMessage());
            errorResponse.setDeletedKeys(new ArrayList<>());
            errorResponse.setDeletedCount(0);
            errorResponse.setUserResults(new HashMap<>());
            errorResponse.setSuccessUserCount(0);
            errorResponse.setFailedUserCount(0);
            return ResultBody.error(-1000_01, "删除用户Redis键失败：" + e.getMessage());
        }
    }

//    @Log("清空Redis缓存")
//    @DeleteMapping(value = "/redis/all")
//    public ResponseEntity deleteAll(){
//        redisService.flushdb();
//        return new ResponseEntity(HttpStatus.OK);
//    }
}
