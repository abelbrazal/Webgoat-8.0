package org.owasp.webgoat.users;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author nbaars
 * @since 3/19/17.
 */
@Controller
@AllArgsConstructor
public class RegistrationController {

    private UserValidator userValidator;


    @GetMapping("/registration")
    public String showForm(UserForm userForm) {
        return "registration";
    }

    @PostMapping(path = "register.mvc")
    @ResponseBody
    public String registration(@ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

       // userService.save(userForm);

      //  securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/welcome";
    }


}
