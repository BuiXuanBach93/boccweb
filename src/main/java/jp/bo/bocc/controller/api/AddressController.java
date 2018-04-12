package jp.bo.bocc.controller.api;

import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.service.AddressService;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by DonBach on 3/16/2017.
 */
@RestController
@RequestMapping("/addresses")
public class AddressController {
    @Autowired
    AddressService service;

    @GetMapping("/{id}")
    @NoAuthentication
    @ResponseBody
    public ShmAddr getAddress(@PathVariable long id) {
        return service.getAddress(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    @NoAuthentication
    @ResponseBody
    public ResponseEntity<List<ShmAddr>> listAddress(@RequestParam(value = "parentId", required = false) Long parentId,
                                                     @RequestParam(value ="isAllIncluded", required = false) Boolean isAllIncluded){
        ResponseEntity<List<ShmAddr>> responseEntity = null;
        List<ShmAddr> list = service.getAddresses(parentId);
        if(isAllIncluded != null && isAllIncluded == true){
            ShmAddr allAddress = new ShmAddr();
            allAddress.setAreaName("すべて");
            allAddress.setAddressId(0L);
            list.add(0,allAddress);
        }
        if(list.isEmpty()){
            return new ResponseEntity<List<ShmAddr>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<ShmAddr>>(list, HttpStatus.OK);
    }

}
