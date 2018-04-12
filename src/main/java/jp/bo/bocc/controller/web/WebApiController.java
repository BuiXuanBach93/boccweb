/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.bo.bocc.controller.web;

import java.util.List;
import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author harunaga
 */
@Controller
@RequestMapping("/api")
public class WebApiController {

    @Autowired
    AddressService addressService;

    @RequestMapping(value = "/addresses", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ShmAddr>> listAddress(@RequestParam(value = "parentId", required = false) Long parentId) {
        return new ResponseEntity<>(addressService.getAddresses(parentId), HttpStatus.OK);
    }

}
