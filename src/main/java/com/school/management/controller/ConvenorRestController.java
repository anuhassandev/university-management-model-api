package com.school.management.controller;

import com.school.management.domain.Convenor;
import com.school.management.domain.Module;
import com.school.management.repo.ConvenorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
public class ConvenorRestController {

    @Autowired
    private ConvenorRepository crepo;

    @GetMapping("/convenors") // list all convenors
	public ResponseEntity<?> listConvenors() {
        List<Convenor> convenors = (List<Convenor>) crepo.findAll();
        if (convenors.isEmpty()) {
            return new ResponseEntity<>("No convenors exist to display", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Convenor>>(convenors, HttpStatus.OK);
    }

    @PostMapping("/convenors") // create a convenor
        public ResponseEntity<?> createConvenor(@RequestBody Convenor convenor, UriComponentsBuilder ucBuilder) {
        if (crepo.existsById(convenor.getId())) {
            return new ResponseEntity<>("Convenor with ID " + convenor.getId() + " already exists", HttpStatus.CONFLICT);
        }
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path("/convenors/{id}").buildAndExpand(convenor.getId()).toUri());
            crepo.save(convenor);
            return new ResponseEntity<Convenor>(convenor, HttpStatus.CREATED);
    }

    @GetMapping("/convenors/{convenorID}") // retrieve a specific convenor
    public ResponseEntity<?> getConvenor(@PathVariable("convenorID") long id) {
        if (crepo.findById(id).isPresent()) {
            Convenor convenor = crepo.findById(id).get();
            return new ResponseEntity<Convenor>(convenor, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Convenor with ID " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/convenors/{convenorID}") // Update a specific convenor
    public ResponseEntity<?> updateConvenor(@PathVariable("convenorID") long id, @RequestBody Convenor convenor) {
        if (crepo.findById(id).isPresent()) {
            Convenor Rconvenor = crepo.findById(id).get();
            Rconvenor.setName(convenor.getName());
            Rconvenor.getModules().clear();
            Rconvenor.getModules().addAll(convenor.getModules());
            Rconvenor.setPosition(convenor.getPosition());
            crepo.save(Rconvenor);
            return new ResponseEntity<Convenor>(Rconvenor, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Convenor with ID " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/convenors/{convenorID}") // delete a specific convenor
    public ResponseEntity<String> deleteConvenor(@PathVariable("convenorID") long id) {
        if (crepo.findById(id).isPresent()) {
            Convenor c = crepo.findById(id).get();
            crepo.delete(c);
            return new ResponseEntity<String>("Convenor with ID " + id + " has been deleted.", HttpStatus.OK);

        } else {
            return new ResponseEntity<String>("Convenor with ID " + id + " not found", HttpStatus.NOT_FOUND);
        }
            //Test to see if modules that only have that convenor are deleted
    //
            //
                //

    }

    @GetMapping("/convenors/{convenorID}/modules") // get all the modules of a specific convenor
    public ResponseEntity<?> getConvenorModules(@PathVariable("convenorID") long id) {
        if (crepo.findById(id).isPresent()) {
            Convenor c = crepo.findById(id).get();
            List<Module> modules = c.getModules();
            if (modules.isEmpty() || modules == null) {
                return new ResponseEntity<String>("Convenor with ID " + id + " has no modules to display", HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<List<Module>>(modules, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<String>("Convenor with ID " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

}
