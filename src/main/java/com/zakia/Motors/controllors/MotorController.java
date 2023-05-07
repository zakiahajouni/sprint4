package com.zakia.Motors.controllors;

import com.zakia.Motors.entities.Motor;
import com.zakia.Motors.entities.Type;
import com.zakia.Motors.service.MotorService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.zakia.Motors.service.TypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class MotorController {
    @Autowired
    MotorService motorService;
    @Autowired
    TypeService typeService;

    @RequestMapping("/showCreate")
    public String showCreate(ModelMap modelMap)
    {   List<Type> types = typeService.getAllTypes();
        modelMap.addAttribute("motor", new Motor());
        modelMap.addAttribute("mode", "new");
        modelMap.addAttribute("types", types);
        modelMap.addAttribute("page",0);
        return "formMotor";
    }
    @RequestMapping("/saveMotor")
    public String saveMotor(@Valid Motor motor,
                              BindingResult bindingResult,
                              @ModelAttribute("page") int pageFromPrevious,
                              @RequestParam (name="size", defaultValue = "2") int size,
                              RedirectAttributes redirectAttributes, ModelMap modelMap)
    {
        int page;
        if (bindingResult.hasErrors()){
            List<Type> types = typeService.getAllTypes();
            modelMap.addAttribute("types",types);
            modelMap.addAttribute("mode","edit");
            return "formMotor";
        }

        if (motor.getIdMotor()==null) //adding
            page = motorService.getAllMotors().size()/size; // calculer le nbr de pages
        else //updating
            page = pageFromPrevious;

        motorService.saveMotor(motor);

        redirectAttributes.addAttribute("page", page);
        return "redirect:/ListeMotors";
    }
    @RequestMapping("/ListeMotors")
    public String listeMotors(ModelMap modelMap,
                                @RequestParam (name="page",defaultValue = "0") int page,
                                @RequestParam (name="size", defaultValue = "2") int size)
    {
        Page<Motor> motos = motorService.getAllMotorsParPage(page, size);
        modelMap.addAttribute("motors", motos);
        modelMap.addAttribute("pages", new int[motos.getTotalPages()]);
        modelMap.addAttribute("currentPage", page);
        return "listeMotors";
    }


    @RequestMapping("/supprimerMotor")
    public String supprimerMotor(@RequestParam("id") Long id,
                                   ModelMap modelMap,
                                   @RequestParam (name="page",defaultValue = "0") int page,
                                   @RequestParam (name="size", defaultValue = "2") int size)
    {
        motorService.deleteMotorById(id);
        Page<Motor> motos = motorService.getAllMotorsParPage(page,
                size);
        modelMap.addAttribute("motors", motos);
        modelMap.addAttribute("pages", new int[motos.getTotalPages()]);
        modelMap.addAttribute("currentPage", page);
        modelMap.addAttribute("size", size);
        return "listeMotors";
    }



    @RequestMapping("/modifierMotor")
    public String editerMotor(@RequestParam("id") Long id, ModelMap modelMap,
                                @RequestParam("page") int page
                                ) {
        Motor motor = motorService.getMotor(id);
        List<Type> types = typeService.getAllTypes();
        modelMap.addAttribute("motor", motor);
        modelMap.addAttribute("types", types);
        modelMap.addAttribute("selectedType", motor.getType());
        modelMap.addAttribute("mode", "edit");
        modelMap.addAttribute("page",page);
        return "formMotor";
    }


    @RequestMapping("/updateMotor")
    public String updateMotor(@ModelAttribute("motor") Motor motor, @ModelAttribute("page") int page,
                              @RequestParam("date") String date, ModelMap modelMap
    ) throws ParseException {
        //conversion de la date
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateCreation = dateformat.parse(String.valueOf(date));
        motor.setDateCreation(dateCreation);

        motorService.updateMotor(motor);
        return "redirect:/ListeMotors";
    }




}