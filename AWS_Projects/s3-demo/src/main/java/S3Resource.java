import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class S3Resource {

    private final S3Client s3;

    public S3Resource(S3Client s3) {
        this.s3 = s3;
    }

    @POST
    @Path("/upload/{bucket}/{key}")
    public Response upload(@PathParam("bucket") String bucket, @PathParam("key") String key ,  Map<String, String> payload){

        String content = payload.get("content");
        s3.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),software.amazon.awssdk.core.sync.RequestBody.fromBytes(content.getBytes(StandardCharsets.UTF_8)));

        return Response.ok().entity("{\"message\":\"Uploaded successfully\"}").build();
    }

    @GET
    @Path("/download/{bucket}/{key}")
    public Response download(@PathParam("bucket") String bucket, @PathParam("key") String key) {
        var obj = s3.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());

        try {
            String data = new String(obj.readAllBytes(), StandardCharsets.UTF_8);
            return Response.ok(data).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
