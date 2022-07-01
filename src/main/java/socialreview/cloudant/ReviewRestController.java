package socialreview.cloudant;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;
import com.cloudant.client.api.model.Response;


@RestController
@RequestMapping("/review")
public class ReviewRestController {

    @Autowired
    private Database db;
    
    
    @RequestMapping(method=RequestMethod.GET, path="/hello")
    public ResponseEntity<String> helloWorld(){
     return ResponseEntity.ok("Hello World Devops");
    }

    // Create a new review
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody String saveReview(@RequestBody Review review) {
        System.out.println("Save Review " + review);
        Response r = null;
        if (review != null) {
            r = db.post(review);
        }
        return r.getId();
    }
    
    
 // Update a  review
    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    public @ResponseBody String updateReview(@RequestBody Review review, @RequestParam Integer itemId) {
        System.out.println("Update Review " + review);
        Response r = null;
        if (review != null) {
            
            List<Review> allDocs = db.findByIndex("{\"itemId\" : " + itemId + "}", Review.class);
        
            for (int i=0; i<allDocs.size(); i++)
            {
            	Review inDBReview= allDocs.get(i);
            	inDBReview.setComment(review.getComment());
            	inDBReview.setReviewer_name(review.getReviewer_name());
                r = db.update(inDBReview);
            	
            }
           // r=db.save(review);
        
         
        }
        else
        {
        	
        	return "Please enter update params";
        }
        return r.getId();
    }

    
 // Update a  review
    @RequestMapping(method = RequestMethod.DELETE, consumes = "application/json")
    public @ResponseBody String deleteReview(@RequestBody Review review, @RequestParam Integer itemId) {
        System.out.println("Update Review " + review);
        Response r = null;
        if (review != null) {
            
            List<Review> allDocs = db.findByIndex("{\"itemId\" : " + itemId + "}", Review.class);
        
            for (int i=0; i<allDocs.size(); i++)
            {
            	Review inDBReview= allDocs.get(i);
            	r=db.remove(inDBReview);
            	
            }
            
        }
        else
        {
        	
        	return "Please enter update params";
        }
        return r.getId();
    }


    
    
    // Query reviews for all documents or by ItemId
    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody List<Review> getAll(@RequestParam(required=false) Integer itemId) {
        // Get all documents from socialreviewdb
        List<Review> allDocs = null;
        try {
            if (itemId == null) {
                allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse()
                            .getDocsAs(Review.class);
            } else {
                // create Index
                // Here is create a design doc named designdoc
                // A view named querybyitemIdView
                // and an index named itemId
                db.createIndex("querybyitemIdView","designdoc","json",
                    new IndexField[]{new IndexField("itemId",SortOrder.asc)});
                System.out.println("Successfully created index");
                allDocs = db.findByIndex("{\"itemId\" : " + itemId + "}", Review.class);
            }
        } catch (Exception e) {
            System.out.println("Exception thrown : " + e.getMessage());
        }
        return allDocs;
    }
}
