import com.example.commit.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CodeCommitTest {


    private static String branchCommitId =""; // needs to be updated to use latest for each test - required for PutFile test
    private static CodeCommitClient codeCommitClient ;
    private static String newRepoName ="";
    private static String existingRepoName ="";
    private static String newBranchName="";
    private static String existingBranchName="";
    private static String commitId ="" ;
    private static String filePath ="";
    private static String email ="";
    private static String name ="";
    private static String repoPath =""; // change for each test
    private static String targetBranch ="";
    private static String prID = "";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        try (InputStream input = CodeCommitClient.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            newRepoName = prop.getProperty("newRepoName");
            existingRepoName = prop.getProperty("existingRepoName");
            newBranchName = prop.getProperty("newBranchName");
            existingBranchName = prop.getProperty("existingBranchName");
            branchCommitId = prop.getProperty("branchCommitId");
            filePath = prop.getProperty("filePath");
            name = prop.getProperty("name");
            repoPath = prop.getProperty("repoPath");
            email = prop.getProperty("email");
            targetBranch = prop.getProperty("targetBranch");
            prID = prop.getProperty("prID");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(codeCommitClient);
        System.out.println("Test 1 passed");
    }

    
}
