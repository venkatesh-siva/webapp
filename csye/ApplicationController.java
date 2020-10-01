package edu.csye.controller;

@RestController
@CrossOrigin("*")
@RequestMapping("/edu")
public class ApplicationController {
	
	@Autowired
	private TurboServices turboServices;
	
	/*
	 * public ApplicationController() { RapidMiner.init(); }
	 */
    
    @PostMapping ("/csye")
    public String requestHandler(@RequestBody String jsonRequest) throws JSONException, IOException, XMLException {
    	System.out.println("here");
    	return turboServices.serviceHandler(jsonRequest);
        
    }
}
