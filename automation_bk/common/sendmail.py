#!/usr/bin/python
# -*- coding:utf-8 -*-

import sys
sys.path.append("/home/autobuild/dbfilegen/")
import pexpect
import sys
reload(sys)
sys.setdefaultencoding("utf-8")
import smtplib
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email.mime.multipart import MIMEMultipart
from email.header import Header
from email.encoders import encode_base64
from email import Utils
from email import Encoders 
import os

sys.setdefaultencoding("utf-8")

STMT_HOST='192.168.105.58'
PORT = 25

COMMASPACE = ", "
class StmtMail:
    def __init__(self):
        self.stmt_host = STMT_HOST
        self.port = PORT
        self.from_user = ''
        self.to_user = ''

    def sendmail(self,from_user, to_user, cc_users, subject, contents, attach):
        
        try:
            print " Mail Send "        
            msg = MIMEMultipart()
            #msg = MIMEMultipart("alternative")
            msg["From"] = from_user
            msg["To"] = to_user
            msg["Cc"] = COMMASPACE.join(cc_users)
            msg["Subject"] = Header(s=subject, charset="utf-8")
            msg["Date"] = Utils.formatdate(localtime = 1)
            msg.attach(MIMEText(contents, "html", _charset="utf-8"))
        
            if (attach != None):
                    part = MIMEBase("application", "octet-stream" ,charset="utf8")
                    part.set_payload(open(attach, "rb").read())
                    Encoders.encode_base64(part)
                    part.add_header("Content-Disposition", 'attachment; filename="%s"' % os.path.basename(attach))
                    msg.attach(part)

            smtp = smtplib.SMTP(self.stmt_host, self.port)
            smtp.sendmail(from_user, cc_users, msg.as_string())
            smtp.close()
        except Exception, e:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            print "[-] Error       : %s " % e
            print "[-] line number :  " , exc_traceback.tb_lineno


if __name__=="__main__":

    try:
        print " Mail Send "

    except Exception, e:
        exc_type, exc_value, exc_traceback = sys.exc_info()
        print "[-] Error       : %s " % e
        print "[-] line number :  " , exc_traceback.tb_lineno
