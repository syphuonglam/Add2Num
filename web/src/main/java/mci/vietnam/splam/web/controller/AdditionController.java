package mci.vietnam.splam.web.controller;

import mci.vietnam.splam.core.domain.BigNumberResult;
import mci.vietnam.splam.core.domain.MyBigNumber;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdditionController {

    private final MyBigNumber myBigNumber = new MyBigNumber();

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @PostMapping("/add")
    public String add(@RequestParam String a, @RequestParam String b, Model model) {
        BigNumberResult result = myBigNumber.sumWithHistory(a.trim(), b.trim());
        model.addAttribute("a", a);
        model.addAttribute("b", b);
        model.addAttribute("result", result.getResult());
        model.addAttribute("steps", result.getSteps());
        return "index";
    }
}
