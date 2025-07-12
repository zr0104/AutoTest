package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class SampleController {

    @RequestMapping("/")
    @ResponseBody
    String home(){
        return "Hello Work!";
    }

    public static void main(String[] args) throws Exception{
//        args = new String[]{"1","2","--name=shienchuang","age=18","--developer.name=shire"};
        SpringApplication.run(SampleController.class, args);
    }
}
