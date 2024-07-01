"""
This Add-On uses EaPDF to convert emails to a PDF archive

"""
import os
import sys
import glob
import subprocess
from documentcloud.addon import AddOn
from clouddl import grab

class EmailArchiver(AddOn):
    """An example Add-On for DocumentCloud."""
    extract_attachments = False

    def check_permissions(self):
        """The user must be a verified journalist to upload a document"""
        self.set_message("Checking permissions...")
        user = self.client.users.get("me")
        if not user.verified_journalist:
            self.set_message(
                "You need to be verified to use this add-on. Please verify your "
                "account here: https://airtable.com/shrZrgdmuOwW0ZLPM"
            )
            sys.exit()

    def fetch_files(self, url):
        """Fetch the files from either a cloud share link or any public URL"""
        self.set_message("Retrieving EML/MSG files...")
        os.makedirs(os.path.dirname("./out/"), exist_ok=True)
        try:
            grab(url, "./out/")
        except HTTPError as http_error:
            self.set_message(
                "There was an issue with downloading emails from the provided URL, please ensure it is public and available."
            )
            print(f"HTTP Error: {http_error}")
            sys.exit(0)
        print("Contents of ./out/ after downloading:")
        print(os.listdir("./out/"))
        os.chdir("out")
        self.strip_white_spaces(os.getcwd())
        os.chdir("..")

    def strip_white_spaces(self, file_path):
        """Strips white space from filename before running it"""
        current_directory = os.getcwd()
        files = os.listdir(current_directory)
        for file_name in files:
            if file_name.strip() != file_name:
                old_file_path = os.path.join(current_directory, file_name)
                new_file_path = os.path.join(current_directory, file_name.strip())
                os.rename(old_file_path, new_file_path)
                # print(f"Renamed: {file_name} -> {file_name.strip()}")

    def eml_to_pdf(self,file_name, output_url):
        os.mkdir("/home/runner/work/email-archiver-addon/email-archiver-addon/output/")
        dotnet_command = (
            f"dotnet ./EaPdfCmd_0.2.6-alpha.2/EaPdfCmd.dll "
            f"-i /home/runner/work/email-archiver-addon/email-archiver-addon/out/{file_name} "
            f"-o /home/runner/work/email-archiver-addon/email-archiver-addon/output/{file_name}/ "
            f"-g '{output_url}'"
        )

        # Execute the dotnet command using subprocess.call
        try:
            subprocess.call(dotnet_command, shell=True)
        except subprocess.CalledProcessError as e:
            print(f"Error running dotnet command: {e}")

    def upload_to_documentcloud(self, file_name, access_level, project_id):
        """Uploads PDF files to DocumentCloud"""
        output_folder = f"/home/runner/work/email-archiver-addon/email-archiver-addon/output/{file_name}/"
        for pdf_file in os.listdir(output_folder):
            if pdf_file.lower().endswith(".pdf") OR pdf_file.lower().endswith(".PDF"):
                file_path = os.path.join(output_folder, pdf_file)
                kwargs = {}  # Define any additional parameters for DocumentCloud upload
                try:
                    self.client.documents.upload(file_path, **kwargs)
                    print(f"Uploaded {pdf_file} to DocumentCloud")
                except Exception as e:
                    print(f"Error uploading {pdf_file} to DocumentCloud: {e}")
    def main(self):
        """The main add-on functionality goes here."""
        output_url = self.data.get("email_archive_url")
        url = self.data["url"]
        self.check_permissions()
        self.fetch_files(url)
        access_level = self.data["access_level"]
        project_id = self.data.get("project_id")
        successes = 0
        errors = 0
        dotnet_test = "dotnet --info"
        os.chdir("out")  # Change context to 'out' directory to look at files we downloaded
        for file_name in os.listdir("."):
            if file_name.lower().endswith(".eml") or file_name.lower().endswith(".mbox"):
                print(f"Processing file: {file_name}")
                self.eml_to_pdf(file_name, output_url)
                self.upload_to_documentcloud(file_name)
    
        #subprocess.call("sudo ln -s /usr/bin/dotnet /usr/local/bin/dotnet", shell=True)
        #dotnet_command = "dotnet ./EaPdfCmd_0.2.6-alpha.2/EaPdfCmd.dll -i /home/runner/work/email-archiver-addon/email-archiver-addon/test.eml -o /home/runner/work/email-archiver-addon/email-archiver-addon -g 'https://example.com'"
        #subprocess.call(dotnet_command, shell=True)
        #print("Current working directory:")
        #print(os.getcwd())
        #print("Contents:"); print("\n".join(os.listdir()))

if __name__ == "__main__":
    EmailArchiver().main()
