package jp.co.metateam.library.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;

/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController {
    
    private final BookMstService bookMstService;

    @Autowired
    public BookController(BookMstService bookMstService){
        this.bookMstService = bookMstService;
    }

    @GetMapping("/book/index")
    public String index(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();
        
        model.addAttribute("bookMstList", bookMstList);

        return "book/index";
    }

    @GetMapping("/book/add")
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }

        return "book/add";
    }
    @PostMapping("/book/add")
    public String register(@Valid @ModelAttribute BookMstDto bookMstDto, BindingResult result, RedirectAttributes ra, Model model ) {
      
        String title = bookMstDto.getTitle();
        String isbn = bookMstDto.getIsbn();

            boolean errtitlenullFlg = false;
            boolean errisbnnullFlg = false;
            boolean errtitlecharacterFlg = false;
            boolean errisbncharacterFlg = false;
            boolean errisbnCharacterType = false;
    
        List<String> errtitleList = new ArrayList<>();
        List<String> errisbnList = new ArrayList<>();
        

            if(title == ""  || title == null){
                errtitleList.add("書籍名は必須です");
                errtitlenullFlg = true;
            }
            if(title == ""  || title == null){
                errisbnList.add("ISBNは必須です");
                errisbnnullFlg = true;
            }

            if(title.length() > 255){
                errtitleList.add("書籍名は255文字以内で入力してください");
                errtitlecharacterFlg = true;
            }
            if(title.length() != 13){
                errisbnList.add("ISBNは13桁で入力してくください");
                errisbncharacterFlg = true;
            }
            //isbnが数値かどうかチェック
            String regex_num = "^[0-9]+$";
            Pattern p1 = Pattern.compile (regex_num);
            Matcher m1 = p1.matcher(isbn);
            boolean isbnCharacterType = m1.matches();

         if(!isbnCharacterType){
            errisbnList.add("ISBNは半角数字で入力してください");
            errisbnCharacterType = true;
         }
        
         if (errtitlenullFlg || errisbnnullFlg || errtitlecharacterFlg || errisbncharacterFlg ||errisbnCharacterType){
                model.addAttribute("errtitle",errtitleList);
                model.addAttribute("errisbn",errisbnList);
            
            return "book/add";
         }

         if(!bookMstService.isbnDuplicateCheck(isbn)){
            model.addAttribute("errisbn", "登録済みのISBNです");
         }

            bookMstService.save(bookMstDto);

            return "redirect:/book/index";

        }
    }
    



