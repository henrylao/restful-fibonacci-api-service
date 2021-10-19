package com.example.restfulfibonacciapiservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * This is the service class controlling the routing
 * when serving fibonacci responses
 */
//@AutoConfigureRestDocs(outputDir = "target/snippets")
@RestController
public class FibonacciController {
    // TODO: can be consolidated under an interface
    public static final String VERSION = "/v1/";
    public static final String RESOURCE = "/fibonacci/";
    public static final String BASE_URI = "/api/" + VERSION + RESOURCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulPrimeApiApplication.class);

    @Autowired
    private FibonacciRepository fibonacciRepository;
//    FibonacciService fibonacciService = FibonacciService

    /**
     * POST method for adding a Fibonacci element into the database
     *
     * @param id
     */
    @PostMapping(BASE_URI + "/add")
    public HttpEntity<? extends Object> addFibonacci(@RequestParam Integer id) throws ExecutionException, InterruptedException, URISyntaxException {
        LOGGER.info("POST: /add");
        Optional<com.example.restfulfibonacciapiservice.Fibonacci> f = fibonacciRepository.findById(id);
        if (!f.isEmpty()) {
            LOGGER.info("Existing fibonacci found. Sending response");
            return getFibonacciById(id);
        } else {
            LOGGER.info("Specified fibonacci at index not found. Computing fibonacci new series and adding to database");
        }
        // create new fibonacci element
        com.example.restfulfibonacciapiservice.FibonacciCalculator fibonacciCalculator = new com.example.restfulfibonacciapiservice.FibonacciCalculator(id);
        BigInteger value = fibonacciCalculator.calculateFibonacci(BigInteger.valueOf(id));
        HashMap<Integer, BigInteger> cache = fibonacciCalculator.getMemo();
        List<com.example.restfulfibonacciapiservice.Fibonacci> fibonacciSeries = new ArrayList<>();
        for (Map.Entry e : cache.entrySet()) {
            fibonacciSeries.add(new com.example.restfulfibonacciapiservice.Fibonacci((Integer) e.getKey(), ((BigInteger) e.getValue())));
        }
        System.out.println("Computed " + fibonacciSeries.size() + " fibonacci elements");
        LOGGER.info("Saved fibonacci values to database");
        fibonacciRepository.saveAll(fibonacciSeries);   // should this be moved?
        URI allFibonacci = new URI(BASE_URI + "/get/all");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(allFibonacci);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }


    /**
     * GET method for reading all Fibonacci elements
     *
     * @return
     */
    @GetMapping(BASE_URI + "/get/all")
    public ResponseEntity<Iterable<com.example.restfulfibonacciapiservice.Fibonacci>> getFibonacci() {
        LOGGER.info("Collecting all fibonacci values");
        try {
            List<com.example.restfulfibonacciapiservice.Fibonacci> fibonacciList = fibonacciRepository.findAll();
            if (fibonacciList.isEmpty()) {
                LOGGER.info("No fibonacci values in database");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            LOGGER.info("All fibonacci values successfully found. Sending response");
            return new ResponseEntity<>(fibonacciList, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.info("Internal error. Sending response");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(BASE_URI + "/update")
    public ResponseEntity<com.example.restfulfibonacciapiservice.Fibonacci> updateFibonaccibyId(@RequestParam Integer id, @RequestParam Integer newValue) {
        Optional<com.example.restfulfibonacciapiservice.Fibonacci> data = fibonacciRepository.findById(id);
        if (!data.isPresent()) {
//            LOGGER.info("Specific fibonacci number was found. Sending response");
//            return new ResponseEntity<>(data.get(), HttpStatus.OK);
            LOGGER.info("Specific fibonacci number was not found. Sending response");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        com.example.restfulfibonacciapiservice.Fibonacci f = data.get();
        f.setValue(BigInteger.valueOf(newValue));

        return new ResponseEntity<>(data.get(), HttpStatus.OK);
    }

    /**
     * TODO: add ability to access subset from the database of fibonacci elements
     *
     * @return
     */
//    @GetMapping(BASE_URI + "/list/{index}")
//    public Iterable<Fibonacci> getFibonacci() {
//        return fibonacciRepository.findAll();
//    }

    /**
     * GET method for reading a Fibonacci element
     *
     * @param id
     * @return
     */
    @GetMapping(BASE_URI + "/get")
    public ResponseEntity<com.example.restfulfibonacciapiservice.Fibonacci> getFibonacciById(@RequestParam Integer id) {
        Optional<com.example.restfulfibonacciapiservice.Fibonacci> data = fibonacciRepository.findById(id);

//        System.out.println(data);
        if (data.isPresent()) {
            LOGGER.info("Specific fibonacci number was found. Sending response");
            return new ResponseEntity<>(data.get(), HttpStatus.OK);
        }
        LOGGER.info("Specific fibonacci number was not found. Sending response");
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(BASE_URI + "/delete")
    public ResponseEntity<HttpStatus> deleteFibonacci(@RequestParam Integer id) {

        com.example.restfulfibonacciapiservice.Fibonacci toDelete = fibonacciRepository.getById(id);
        if (toDelete.equals(null)) {
            LOGGER.info("Specific fibonacci number was not found. No deletion. Sending response");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
//        fibonacciRepository.delete(toDelete);
//        fibonacciRepository.saveAll(fibonacciRepository.findAll());
        try {
            LOGGER.info("Specific fibonacci number was found. Deleting. Sending response");
            fibonacciRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.info("Internal error. Sending response");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Transactional
//    @Modifying
    @DeleteMapping(BASE_URI + "/delete/all")
    public ResponseEntity<HttpStatus> deleteAllFibonacci() {
        if (fibonacciRepository.findAll().isEmpty()) {
            LOGGER.info("No fibonacci elements found in database. No deletion. Sending response");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
//    public void deleteAllFibonacci() {
        try {
//            if (fibonacciRepository.findAll().size() == 0){
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//            fibonacciRepository.deleteAll();
            fibonacciRepository.deleteAllInBatch();
            LOGGER.info("All fibonacci values were successfully found. Deleted all fibonacci values in database. Sending response");
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            LOGGER.info("Internal error. Sending response");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
//        fibonacciRepository.deleteAll();
//        fibonacciRepository.deleteAll(fibonacciRepository.findAll());
//        fibonacciRepository.saveAll(fibonacciRepository.findAll());
    }

}
