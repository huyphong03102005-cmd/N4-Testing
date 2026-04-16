import os
import glob

replacements = {
    "'Picture/Logo_bar.png'": "'./Picture/Logo_bar.png'",
    "'Picture/Background.png'": "'./Picture/Background.png'",
    '\"Picture/Pictuer_1.jpg\"': '\"./Picture/Pictuer_1.jpg\"',
    '\"Picture/Pictuer_2.jpg\"': '\"./Picture/Pictuer_2.jpg\"',
    '\"Picture/Logo_signin.png\"': '\"./Picture/Logo_signin.png\"',
    '\"QR_CODE.png\"': '\"./Picture/QR_CODE.png\"',
    '\"./bill_logo.png\"': '\"./Picture/bill_logo_logo.png\"',
    '\"./Logo_bar.png\"': '\"./Picture/Logo_bar.png\"'
}

directory = r"c:\Users\PC\Downloads\front-end_Final-20260416T030729Z-3-001\front-end_Final\front end"
for ext in ["*.html", "*.css", "*.js"]:
    for filepath in glob.glob(os.path.join(directory, ext)):
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()
        
        new_content = content
        for old, new in replacements.items():
            new_content = new_content.replace(old, new)
            
        if content != new_content:
            with open(filepath, "w", encoding="utf-8") as f:
                f.write(new_content)
            print("Updated " + os.path.basename(filepath))
