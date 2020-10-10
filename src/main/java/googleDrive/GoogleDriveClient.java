package googleDrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GoogleDriveClient {

    private static final String APPLICATION_NAME = "AnimeBotRGA";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final java.io.File CREDENTIALS_FOLDER = new java.io.File("credentials");

    private static final String CLIENT_SECRET_FILE_NAME = "client_secret.json";

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static FileDataStoreFactory DATA_STORE_FACTORY;

    private static HttpTransport HTTP_TRANSPORT;

    private static Drive _driveService;

    public static void init()
    {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(CREDENTIALS_FOLDER);
            Credential credential = getCredentials();
            _driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                    .setApplicationName(APPLICATION_NAME).build();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static File createGoogleFile(
            String googleFolderIdParent, String contentType,
            String customFileName, java.io.File uploadFile
    ) throws IOException {

        AbstractInputStreamContent uploadStreamContent = new FileContent(contentType, uploadFile);
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    public static File getGoogleSubFolderByName(
            String googleFolderIdParent,
            String subFolderName
    )
            throws IOException {
        String query = QueryCreator
                .createQuery(subFolderName, "application/vnd.google-apps.folder", googleFolderIdParent);
        var folders = getFiles(query);
        if (folders.size() == 0)
            return null;
        if (folders.size() != 1)
            throw new IOException("You have more than one folder with name:" + subFolderName);
        return folders.get(0);
    }

    public static File getFileByName(
            String fileName,
            String mimeType,
            String folderId
    )
            throws IOException {
        String query = QueryCreator.createQuery(fileName, mimeType, folderId);
        var files = getFiles(query);
        if (files.size() == 0)
            return null;
        return files.get(0);
    }

    private static Drive getDriveService() throws IOException {
        if (_driveService == null) {
            throw new IOException("Client not initialized");
        }
        return _driveService;
    }

    private static Credential getCredentials() throws IOException {

        java.io.File clientSecretFilePath = new java.io.File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME);

        if (!clientSecretFilePath.exists()) {
            throw new FileNotFoundException("Please copy " + CLIENT_SECRET_FILE_NAME //
                    + " to folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
        }

        InputStream in = new FileInputStream(clientSecretFilePath);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    private static File _createGoogleFile(
            String googleFolderIdParent, String contentType,
            String customFileName, AbstractInputStreamContent uploadStreamContent
    )
            throws IOException {

        List<String> parents = Collections.singletonList(googleFolderIdParent);

        File fileMetadata = new File();
        fileMetadata.setName(customFileName);
        fileMetadata.setParents(parents);
        fileMetadata.setMimeType(contentType);

        Drive driveService = getDriveService();

        File file =
                driveService.files().create(fileMetadata, uploadStreamContent)
                .setFields("id, mimeType, webContentLink, webViewLink, parents").execute();

        return file;
    }

    private static List<File> getFiles(String query) throws IOException {
        Drive driveService = getDriveService();
        String pageToken = null;
        List<File> files
                = new ArrayList<File>();
        do {
            FileList result = driveService.files().list().setQ(query).setSpaces("drive") //
                    // Fields will be assigned values: id, name, createdTime
                    .setFields("nextPageToken, files(id, name, webContentLink, webViewLink)")//
                    .setPageToken(pageToken).execute();
            files.addAll(result.getFiles());
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        return files;
    }
}