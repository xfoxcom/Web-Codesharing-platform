package platform;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class ControllerOfCode {
    private List<Code> codes = new ArrayList<>();
    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";

    @Autowired
    CodeRepository repository;
    public ControllerOfCode(CodeRepository codeRepository) {
        this.repository = codeRepository;
    }
    @GetMapping(value = "/code/{N}", produces = MediaType.TEXT_HTML_VALUE)
    public String getCode(HttpServletResponse response, @PathVariable int N, Model model) {
        response.addHeader("Content-Type", "text/html");

        model.addAttribute("code", repository.findById(N).get());
        return "getSnippet";
    }

    @GetMapping(value = "/api/code/{N}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Code getField(HttpServletResponse response, @PathVariable int N) {
        response.addHeader("Content-Type", "application/json");

        if (repository.existsById(N)) {
            return repository.findById(N).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/code/new")
    @ResponseBody
    public ResponseEntity<Map<String, String>> postNewCode(@RequestBody Code code) {

        // TODO: 03.07.2022 delete after test
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        code.setDate(LocalDateTime.now());
        repository.save(code);
        return ResponseEntity.ok(Map.of("id", String.valueOf(code.getId())));
    }

    @GetMapping("/code/new")
    public String createCode() {
        return "create";
    }

    @GetMapping("/api/code/latest")
    @ResponseBody
    public List<Code> getLatest() {
       return repository.findAll().stream().sorted(Comparator.comparing(Code::getDate).reversed()).limit(10).collect(Collectors.toList());
    }
    @GetMapping("/code/latest")
    public String getLatestHTML (Model model) {
        model.addAttribute("codes", repository.findAll().stream().sorted(Comparator.comparing(Code::getDate).reversed()).limit(10).collect(Collectors.toList()));
        return "latest";
    }
}
