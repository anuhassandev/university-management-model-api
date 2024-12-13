package com.school.management.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.school.management.domain.Convenor;
import com.school.management.domain.Module;
import com.school.management.domain.Session;
import com.school.management.repo.ConvenorRepository;
import com.school.management.repo.ModuleRepository;
import com.school.management.repo.SessionRepository;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class ModuleRestController {

    @Autowired
    private ConvenorRepository crepo;

    @Autowired
    private ModuleRepository mrepo;
    
    @Autowired
    private SessionRepository srepo;

    @GetMapping("/modules") // list all modules
    public ResponseEntity<?> listmodules() {
        List<Module> modules = (List<Module>) mrepo.findAll();
        if (modules.isEmpty()) {
            return new ResponseEntity<>("No modules exist to display", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Module>>(modules, HttpStatus.OK);
    }

    @PostMapping("/modules") // create a module
    public ResponseEntity<?> createModule(@RequestBody Module m, UriComponentsBuilder ucBuilder) {
        if (mrepo.existsById(m.getCode())) {
            return new ResponseEntity<>("Module with code " + m.getCode() + " already exists", HttpStatus.CONFLICT);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/modules/{code}").buildAndExpand(m.getCode()).toUri());
        mrepo.save(m);
        return new ResponseEntity<Module>(m, HttpStatus.CREATED);
    }

    @GetMapping("/modules/{moduleCode}") // retrieve a specific module
    public ResponseEntity<?> getModule(@PathVariable("moduleCode") String code) {
        if (mrepo.findById(code).isPresent()) {
            Module m = mrepo.findById(code).get();
            return new ResponseEntity<Module>(m, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Module with code " + code + " not found", HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping ("/modules/{moduleCode}") // Update a specific module
    public ResponseEntity<?> updateModule(@PathVariable("moduleCode") String code, @RequestBody Map<String, Object> fields) {

        if (mrepo.findById(code).isPresent() == false) {
            return new ResponseEntity<>("Module with code " + code + " not found", HttpStatus.NOT_FOUND);
        }

        Module module = mrepo.findById(code).get();



        //remove moduleCode m field because we don't change this
        fields.remove("moduleCode");

        //find each "oldValue" field on the object and set it to the "newValue" field using reflection
        Module finalModule = module;
        fields.forEach((oldValue, newValue) -> {
            Field field = ReflectionUtils.findField(Module.class, oldValue); // find the oldValue in the object
            field.setAccessible(true);
            ReflectionUtils.setField(field, finalModule, newValue); // set the field in the module to the new value
        });

        module = mrepo.save(module);
        return new ResponseEntity<Module>(module, HttpStatus.OK);
    }



    @DeleteMapping("/modules/{moduleCode}") // delete a specific module
    public ResponseEntity<String> deleteModule(@PathVariable("moduleCode") String code) {
        if (mrepo.findById(code).isPresent()) {
            Module m = mrepo.findById(code).get();

            // removing annoying foreign key constraint for DATA INTEGRITY
            List<Convenor> module_convenors = (List<Convenor>) crepo.findAll();
            for (Convenor c: module_convenors) {
                if (c.getModules().contains(m)) {
                    c.getModules().remove(m);
                }
            }
            mrepo.delete(m);
            return new ResponseEntity<String>("Module with code " + code + " has been deleted.", HttpStatus.OK);

        } else {
            return new ResponseEntity<String>("Module with code " + code + " not found", HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/modules/{moduleCode}/sessions") // list all sessions in a module
    public ResponseEntity<List<Session>> listSessions(@PathVariable("moduleCode") String moduleCode) {
        Module m = mrepo.findById(moduleCode).get();
        List<Session> sessions = (List<Session>) m.getSessions();
        if (sessions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);
    }

    @PostMapping("/modules/{moduleCode}/sessions") // create a session in a module
    public ResponseEntity<?> createSession(@PathVariable("moduleCode") String moduleCode, @RequestBody Session s, UriComponentsBuilder ucBuilder) {
        if (srepo.existsById(s.getId())) {
            return new ResponseEntity<>("Session with ID " + s.getId() + " already exists", HttpStatus.CONFLICT);
        }
        Module m = mrepo.findById(moduleCode).get();
        srepo.save(s);
        m.getSessions().add(s);
        mrepo.save(m);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/modules/{code}").buildAndExpand(s.getId()).toUri());
        return new ResponseEntity<Module>(m, HttpStatus.CREATED);
    }

    @GetMapping("/modules/{moduleCode}/sessions/{sessionID}") // retrieve a session in a module
    public ResponseEntity<?> getSession(@PathVariable("moduleCode") String code, @PathVariable("sessionID") long id) {
        if (mrepo.findById(code).isPresent()) {
            Module m = mrepo.findById(code).get();
            List<Session> sessions = m.getSessions();
            for (Session s : sessions) {
                long sid = s.getId();
                if (sid == id) {
                    return new ResponseEntity<Session>(s, HttpStatus.OK);
                }
            }
        }
            return new ResponseEntity<>("Session with ID " + id + " not found", HttpStatus.NOT_FOUND);
        }

    @PutMapping("/modules/{moduleCode}/sessions/{sessionID}") // Update a session in a module using PUT MAPPING
    public ResponseEntity<?> updateMoudulePUT(@PathVariable("moduleCode") String code, @PathVariable("sessionID") long id, @RequestBody Session session) {
        if (mrepo.findById(code).isPresent()) {
            Module m = mrepo.findById(code).get();
            List<Session> sessions = m.getSessions();
            for (Session s : sessions) {
                long sid = s.getId();
                if (sid == id) {
                    s.setDatetime(session.getDatetime());
                    s.setDuration(session.getDuration());
                    s.setTopic(session.getTopic());
                    s = srepo.save(s);
                    return new ResponseEntity<Session>(s, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("Session with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/modules/{moduleCode}/sessions/{sessionID}") // Update a session in a module using PATCH MAPPING
    public ResponseEntity<?> updateMoudulePATCH(@PathVariable("moduleCode") String code, @PathVariable("sessionID") long id, @RequestBody Map<String, Object> fields) throws ParseException {

        if (srepo.findById(id).isPresent() == false) {
            return new ResponseEntity<String>("Session with ID " + id + " not found", HttpStatus.NOT_FOUND);
        }

        Module module = mrepo.findById(code).get();
        Session session = srepo.findById(id).get();

        //remove id field because we don't change this
        fields.remove("id");

        //find each "oldValue" field on the object and set it to the "newValue" field using reflection
        List<Session> sessions = module.getSessions();
        for (Session s : sessions) {
            long sid = s.getId();
            if (sid == id) {
                Session finalSession = session;

                for (var entry: fields.entrySet())
                {
                    // if we're dealing with the datetime field do the conversions ourselves
                    if (entry.getKey() == "datetime") {
                        String datetime_as_string = entry.getValue().toString();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        try {
                            Date parsedDate = dateFormat.parse(datetime_as_string);
                            Timestamp datetime = new Timestamp(parsedDate.getTime());
                            entry.setValue(datetime); // make it a Timestamp object TO AVOID TYPE CONVERSION ERROR
                        }
                        catch(ParseException e) {
                            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                            Date parsedDate = dateFormat.parse(datetime_as_string);
                            Timestamp datetime = new Timestamp(parsedDate.getTime());
                            entry.setValue(datetime); // make it a Timestamp object TO AVOID TYPE CONVERSION ERROR
                        }


                    }
                }
                fields.forEach((oldValue, newValue) -> {
                    Field field = ReflectionUtils.findField(Session.class, oldValue); // find the oldValue in the object
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, finalSession, newValue); // set the field in the module to the new value
                });
            }
        }
        session = srepo.save(session);
        return new ResponseEntity<Session>(session, HttpStatus.OK);

    }

    @DeleteMapping("/modules/{moduleCode}/sessions/{sessionID}") // delete a specific session from a module
    public ResponseEntity<String> deleteSession(@PathVariable("moduleCode") String code, @PathVariable("sessionID") long id) {
        if (mrepo.findById(code).isPresent()) {
            Module m = mrepo.findById(code).get();
            List<Session> sessions = m.getSessions();
            for (Session s : sessions) {
                long sid = s.getId();
                if (sid == id) {
                    srepo.delete(s);
                    return new ResponseEntity<String>("Session with ID " + id + " has been deleted.", HttpStatus.OK);
                }
            }

        }
            return new ResponseEntity<String>("Session with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }


}
