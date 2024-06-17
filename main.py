"""
This Add-On uses EaPDF to convert emails to a PDF archive

"""
import os
import sys
import subprocess
from documentcloud.addon import AddOn


class EmailArchiver(AddOn):
    """An example Add-On for DocumentCloud."""

    def main(self):
        """The main add-on functionality goes here."""
        dotnet_test = "dotnet --info"
        dotnet_command = "dotnet EaPdfCmd.dll -i /home/runner/work/email-archiver-addon/email-archiver-addon/test.eml -o /home/runner/work/email-archiver-addon/email-archiver-addon -g 'https://example.com'"
        subprocess.call(dotnet_command, shell=True)
        #print("Current working directory:")
        #print(os.getcwd())
        #print("Contents:"); print("\n".join(os.listdir()))

if __name__ == "__main__":
    EmailArchiver().main()
