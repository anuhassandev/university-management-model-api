package com.school.management.controller;

import com.school.management.domain.Convenor;
import com.school.management.domain.Module;
import com.school.management.domain.Session;
import com.school.management.repo.ConvenorRepository;
import com.school.management.repo.ModuleRepository;
import com.school.management.repo.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SessionRestController {

    @Autowired
    private SessionRepository r;

    @Autowired
    private ConvenorRepository crepo;

    @Autowired
    private ModuleRepository mrepo;

    @DeleteMapping("/sessions") // delete all sessions
    public ResponseEntity<String> deleteSession() {
        List<Session> sessions = (List<Session>) r.findAll();
        if (sessions.isEmpty()) {
            return new ResponseEntity<>("No sessions exist to delete", HttpStatus.NO_CONTENT);
        } else {
            r.deleteAll();
            return new ResponseEntity<>("All sessions deleted", HttpStatus.OK);
        }
    }

    @GetMapping("/sessions") // list all sessions, filtering by convenor ID, Module Code or both or neither
    public ResponseEntity<?> listSessions(@RequestParam(value="convenorID", required = false) Long convenorID, @RequestParam(value="moduleCode", required = false) String moduleCode) {

        if (convenorID == null && moduleCode == null) { //no request params
            // return all sessions that exist
            List<Session> sessions = (List<Session>) r.findAll();
            if (sessions.isEmpty()) {
                return new ResponseEntity<>("No sessions to show", HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);
        }

        else if (convenorID != null && moduleCode == null) { //only convenorID is used as a filter
            Convenor convenor = crepo.findById(convenorID).get();
            List<Module> modules = convenor.getModules();
            List<Session> sessions = new ArrayList<Session>();
            for (Module m : modules) {
                sessions.addAll(m.getSessions());
            }
            if (sessions.isEmpty()) {
                return new ResponseEntity<>("No sessions to show", HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);

        }

        else if (convenorID == null && moduleCode != null) { //only moduleCode is used as a filter
            Module module = mrepo.findById(moduleCode).get();
            List <Session> sessions = module.getSessions();
            if (sessions.isEmpty()) {
                return new ResponseEntity<>("No sessions to show", HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);

        }

        else if (convenorID != null && moduleCode != null) { //both request params
            Convenor convenor = crepo.findById(convenorID).get();
            List<Module> convenor_modules = convenor.getModules();
            Module target_module = new Module();
            for (Module m: convenor_modules){
                if (m.getCode().equals(moduleCode)) {
                    target_module = m;
                }
            }
            List<Session> sessions = target_module.getSessions();
            if (sessions.isEmpty()) {
                return new ResponseEntity<>("No sessions to show", HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);

        }

        else {
            return new ResponseEntity<>("No sessions to display", HttpStatus.NO_CONTENT);
        }


    }

}
