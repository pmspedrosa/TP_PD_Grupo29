package com.isec.pd_tp.controllers;

//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("hello-world")
public class HelloWorldController {

    @GetMapping("hello-world/{lang}")
    //@GetMapping("{lang}")
    public String get2(@PathVariable("lang") String language,
                       @RequestParam(value="name", required = false) String provided_name/*,
            @RequestHeader("Authorization") String token*/) {

        switch(language.toUpperCase()){
            case "PT" -> {return (provided_name==null)?"Ola' Mundo!":"Ola' "+provided_name+"!";}
            case "EN" -> {return (provided_name==null)?"Hello World!":"Hello "+provided_name+"!";}
            case "ES" -> {return (provided_name==null)?"Hola Mundo!":"Hola "+provided_name+"!";}
            default -> {return "Not supported language!";}
        }
    }

    @GetMapping("hello-world")
    //@GetMapping("")
    public String get3(@RequestParam(value="name", required=false) String name) {
        return (name==null)?"Salut tout le monde!":"Salut "+name+"!";
    }

    //Alternativa baseada num unico metodo e com retorno do tipo ResponseEntity
    /*
    @GetMapping(value={"hello-world","hello-world/{lang}"})
    public ResponseEntity get1(@PathVariable(value="lang", required=false) String language,
            @RequestParam(value="name", required = false) String provided_name) {

        if(language==null)
            return (provided_name==null)?ResponseEntity.ok("Salut tout le monde!"):
                    ResponseEntity.ok("Salut "+provided_name+"!");

        switch(language.toUpperCase()){
            case "PT" -> {return (provided_name==null)?ResponseEntity.ok("Ola' Mundo!"):
                    ResponseEntity.ok("Ola' "+provided_name+"!");}
            case "EN" -> {return (provided_name==null)?ResponseEntity.ok("Hello World!"):
                    ResponseEntity.ok("Hello "+provided_name+"!");}
            case "ES" -> {return (provided_name==null)?ResponseEntity.ok("Hola Mundo!"):
                    ResponseEntity.ok("Hola "+provided_name+"!");}
            default -> {return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not supported language!");}
        }

    }
    */

}
