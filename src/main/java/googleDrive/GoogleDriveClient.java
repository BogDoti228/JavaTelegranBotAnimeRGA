package googleDrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.*;
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


public enum GoogleDriveClient {
    INSTANCE;

    private final String APPLICATION_NAME = "AnimeBotRGA";

    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final java.io.File CREDENTIALS_FOLDER = new java.io.File("credentials");

    private final String CLIENT_SECRET_FILE_NAME = "client_secret.json";

    private final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private FileDataStoreFactory DATA_STORE_FACTORY;

    private HttpTransport HTTP_TRANSPORT;

    private Drive _driveService;

    public void init()
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

    public File createGoogleFile(
            String googleFolderIdParent, String contentType,
            String customFileName, java.io.File uploadFile
    ) throws IOException {

        AbstractInputStreamContent uploadStreamContent = new FileContent(contentType, uploadFile);
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    public File createGoogleFile(
            String googleFolderIdParent, String contentType,
            String customFileName, byte[] uploadData
    ) throws IOException {
        AbstractInputStreamContent uploadStreamContent = new ByteArrayContent(contentType, uploadData);
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    public File createGoogleFile(
            String googleFolderIdParent, String contentType,
            String customFileName, InputStream uploadData
    ) throws IOException {
        AbstractInputStreamContent uploadStreamContent = new InputStreamContent(contentType, uploadData);
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    public File getGoogleSubFolderByName(
            String googleFolderIdParent,
            String subFolderName
    )
            throws IOException {
        String query = QueryCreator
                .QUERY_CREATOR.createQuery(subFolderName, "application/vnd.google-apps.folder", googleFolderIdParent);
        var folders = getFilesByQuery(query);
        if (folders.size() == 0)
            return null;
        if (folders.size() != 1)
            throw new IOException("You have more than one folder with name:" + subFolderName);
        return folders.get(0);
    }

    public File getFileByName(
            String fileName,
            String mimeType,
            String folderId
    )
            throws IOException {
        String query = QueryCreator.QUERY_CREATOR.createQuery(fileName, mimeType, folderId);
        var files = getFilesByQuery(query);
        if (files.size() == 0)
            return null;
        return files.get(0);
    }

    public List<File> getAllFilesInFolder(String folderId) throws IOException {
        var query = QueryCreator.QUERY_CREATOR.createQuery(null,null, folderId);
        return getFilesByQuery(query);
    }

    public void deleteFile(String fileId){
        try {
            File newContent = new File();
            newContent.setTrashed(true);
            _driveService.files().update(fileId, newContent).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Drive getDriveService() throws IOException {
        if (_driveService == null) {
            throw new IOException("Client not initialized");
        }
        return _driveService;
    }

    private Credential getCredentials() throws IOException {

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

    private File _createGoogleFile(
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
                .setFields("id, mimeType, webContentLink, webViewLink, parents, name").execute();

        return file;
    }

    private List<File> getFilesByQuery(String query) throws IOException {
        Drive driveService = getDriveService();
        String pageToken = null;
        List<File> files
                = new ArrayList<File>();
        do {
            FileList result = driveService.files().list().setQ(query).setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, webContentLink, webViewLink)")
                    .setPageToken(pageToken).execute();
            files.addAll(result.getFiles());
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        return files;
    }
}

