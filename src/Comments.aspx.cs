using System;
using System.IO;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace YourNamespace
{
    public partial class CommentViewer : System.Web.UI.Page
    {
        private string commentFolderPath;
        protected void Page_Init(object sender, EventArgs e)
        {
            commentFolderPath = Server.MapPath("~/App_Data/Comments/");
            if (!Directory.Exists(commentFolderPath))
            {
                Directory.CreateDirectory(commentFolderPath);
            }
        }

        protected void Page_Load(object sender, EventArgs e)
        {
            string fileName = Request.QueryString["file"];

            if (string.IsNullOrEmpty(fileName))
            {
                ListCommentFiles();
            }
            else
            {
                ServeCommentAsPage(fileName);
            }
        }

        protected void btnSubmit_Click(object sender, EventArgs e)
        {
            if (!string.IsNullOrEmpty(txtComment.Text))
            {
                string newFileName = Guid.NewGuid().ToString() + ".txt";
                string fullPath = Path.Combine(commentFolderPath, newFileName);
                File.WriteAllText(fullPath, txtComment.Text);
            }
            Response.Redirect(Request.Url.LocalPath, true);
        }

        private void ListCommentFiles()
        {
            phCommentLinks.Controls.Clear();
            phCommentLinks.Controls.Add(new LiteralControl("<ul>"));
            DirectoryInfo di = new DirectoryInfo(commentFolderPath);
            foreach (FileInfo fi in di.GetFiles("*.txt"))
            {
                HyperLink fileLink = new HyperLink();
                fileLink.Text = "查看評論 " + fi.Name;
                fileLink.NavigateUrl = Request.Url.LocalPath + "?file=" + fi.Name;
                
                var listItem = new LiteralControl("<li>");
                phCommentLinks.Controls.Add(listItem);
                phCommentLinks.Controls.Add(fileLink);
                phCommentLinks.Controls.Add(new LiteralControl("</li>"));
            }
            phCommentLinks.Controls.Add(new LiteralControl("</ul>"));
        }

        private void ServeCommentAsPage(string fileName)
        {
            try
            {
                string fullPath = Path.Combine(commentFolderPath, fileName);
                string commentContent = File.ReadAllText(fullPath);

                Response.Clear();
                Response.Write(commentContent);
                Response.End();
            }
            catch (Exception ex)
            {
                Response.Write("無法讀取評論" );
            }
        }
    }
}