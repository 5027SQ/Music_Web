
package com.example.music.controller;
import com.example.music.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Test", description = "测试接口")
@RestController
@RequestMapping("/test")
public class TestController {

  @GetMapping("/ping")
  public Result<String> ping(){
    return Result.ok("music server running");
  }
}
