#!/usr/bin/python
# -*- coding: utf-8 -*-
import sys
sys.path.append("/home/dbqa/automation/")
sys.path.append("/home/dbqa/automation/common/")
import pexpect
import tarfile
import subprocess
import re
import os
import getopt
import traceback
import shutil
import commands
from BeautifulSoup import *
from init import *
from sendmail import *
from refresh import *
from commutil import *
import sys
reload(sys)
sys.setdefaultencoding("utf-8")

PROMPT = ['# ','>>> ','> ','\$ ','\] ']
FILE_GEN_HOME = '/home/dbqa/workspace/Prosync_Regression_Test'

REPORT_LIST = [ 'configM_105_48.xml' ]

WORKSPACE = '/home/dbqa/workspace/Prosync_Regression_Test'


def usage():
    print "daily Test [options] configfile   "
    print "-h : help"
    print "-C : config file: configM.xml"
    print "-P : config file: pom.xml"


class Report:
    def __init__(self):
        self.connection1 = ""
        self.connection2 = ""
        self.out_inernal1 = ""
        self.out_sigsegv1 = ""
	self.revision = ""
	self.content = ""
	self.to_whom ="sangbok_heo@tmax.co.kr"
        self.style="<style type=\"text/css\"> \
                    .tg  {border-collapse:collapse;border-spacing:0;}\
                    .tg td{font-family:Arial, sans-serif;font-size:14px;padding:3px 20px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}\
                    .tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:3px 20px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}\
                    .tg .tg-9hbo{font-weight:bold;vertical-align:top} \
                    </style>"

        self.head="<table class=\"tg\"> \
                   <tr class=\"head\">  \
                    <th class=\"tg-9hbo\">Source Tibero Result</th> \
                    <th class=\"tg-9hbo\">Source Oracle Result</th> \
                   </tr> "
        self.tail="</table> \
                   <html>"


    def show(self):
        print "connection1 : %s " % self.connection1
        print "out_inernal1 : %s " % self.out_inernal1
        print "out_sigsegv1 : %s " % self.out_sigsegv1


    def makeReport(self ,server_info ):

        try:
            #print "Make Report file (Body)"
            body="   <tr class=\"tg-9hbo\"> \
                     <td class=\"tg-9hbo\">%s</td> \
                     <td class=\"tg-9hbo\">%s</td> \
                   </tr> " % (self.out_inernal1 , \
                              self.out_sigsegv1 )


            #report_html=style + head + body + tail
            return body
        except Exception, e:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            print "[-] Error       : %s " % e
            print "[-] line number :  " , exc_traceback.tb_lineno







def outfileParse(outfileStr):
    outfiles=""
    try:
        lines = outfileStr.splitlines()
        linecnt = 1
        length = len(lines)
        while linecnt < 50:
            linecnt = linecnt + 1
            if linecnt > length:
            	  break
            if bool(re.search('(mthr exited abnormally)', lines[linecnt-1])):
                continue

            outfiles = outfiles + '<br>'+ lines[linecnt-1]

    except Exception, e:
        outfiles = " outfiles  is normal"
        exc_type, exc_value, exc_traceback = sys.exc_info()
        print "[-] Error       : %s " % e
        print "[-] line number :  " , exc_traceback.tb_lineno

    return outfiles

def valueParse(outfileStr):
    outfiles=""
    try:
        lines = outfileStr.splitlines()
        linecnt = 1
        length = len(lines)
	outfiles = lines[1]

    except Exception, e:
        outfiles = " outfiles  is normal"
        exc_type, exc_value, exc_traceback = sys.exc_info()
        print "[-] Error       : %s " % e
        print "[-] line number :  " , exc_traceback.tb_lineno

    return outfiles


if __name__=="__main__":

    try:
        opts, args = getopt.getopt(sys.argv[1:],
                                   "hc:s:vrpPO:Ri:dq:C:tl:xXT:VI:kQg:m:fDw:")

    except IOError as e:
        usage()
        print ("[-] Error : "  + e)
        sys.exit(2)

    #option default value
    option_config = "%s/config/configM.xml" %  FILE_GEN_HOME

    for o, a in opts:
        if o == "-h":
            print "======= options ========"
            usage()
            sys.exit(0)
        elif o == "-C":

            option_config = "%s/config/%s" % (FILE_GEN_HOME , a)
            #target_pom_path="/home/autobuild/remoteinstall/workspace/%s" % os.path.basename(option_config).replace(".xml","").replace("configM","JOB")
            target_build_path="/home/autobuild/remoteinstall/workspace/%s" % os.path.basename(option_config).replace(".xml","").replace("configM","JOB")
            print "options -C :%s" % option_config
        elif o == "-P":
            option_pompath = a
            print "options -P :%s" % target_build_path
        elif o == "-A":
            option_pompath = a
            print "options -A :%s" % target_build_path

    try:

        html_body =""
        os.chdir(FILE_GEN_HOME+"/report")
        for conf_file in REPORT_LIST:
            #print conf_file

            conf_file =  "%s/config/%s" % (FILE_GEN_HOME,conf_file)
            info_xml = open(conf_file).read()
            info_soup = BeautifulSoup(info_xml)


            #config file & install file make
            server_info = Conf(info_soup,conf_file)
            #server_info.show()

            report = Report()
	   
	    if len(sys.argv) == 2:
		report.to_whom = sys.argv[1]

            print " ============ result check ========="
            try:
                #remoteserver_conn = PsshConn(server_info.tac_server_ip , server_info.tac_server_id , server_info.tac_server_passwd,60).sshconn()
		remoteserver_conn = PsshConn2("192.168.105.181" , "prosync" , 60).sshconn()

    
                find_internal ='cat /home/prosync/dml_case/TIBERO_*'
                find_sigsegv ='cat /home/prosync/dml_case/ORACLE_*'
                outfile_list =""
    
                remoteserver_conn.sendline(find_internal)
                remoteserver_conn.expect(PROMPT)
                report.out_inernal1 = outfileParse(remoteserver_conn.before)
		
		if bool(re.search('(FAIL)', report.out_inernal1)):
			report.out_inernal1 = "<h2> Result : <font color=red>FAIL</font></h2><br>" + report.out_inernal1
		else:
			report.out_inernal1 = "<h2> Result : <font color=blue>PASS</font></h2><br>" + report.out_inernal1

                remoteserver_conn.sendline(find_sigsegv)
                remoteserver_conn.expect(PROMPT)
                report.out_sigsegv1 = outfileParse(remoteserver_conn.before)
		
		if bool(re.search('(FAIL)', report.out_sigsegv1)):
			report.out_sigsegv1 = "<h2> Result : <font color=red>FAIL</font></h2><br>" + report.out_sigsegv1
		else:
			report.out_sigsegv1 = "<h2> Result : <font color=blue>PASS</font></h2><br>" + report.out_sigsegv1
    
		### add

		find_rev = 'ls /home/prosync/dml_case/TIBERO_* | cut -c31-34'
                remoteserver_conn.sendline(find_rev)
                remoteserver_conn.expect(PROMPT)
                #report.revision = outfileParse(remoteserver_conn.before)
                report.revision = valueParse(remoteserver_conn.before)
		
		print 'revision : ' +  report.revision 
		print 'to : ' + report.to_whom
		report.content = 'Prosync4 r' + report.revision + ' �ڵ�ȭ �׽�Ʈ ��� �Դϴ�.<br><br>'

                html_body = html_body + report.makeReport( server_info)
                html_reports=report.content.decode("euc-kr") + report.style.decode("euc-kr") + report.head.decode("euc-kr")+ html_body.decode("euc-kr")  + report.tail.decode("euc-kr")

            except Exception ,e:
                 continue;    

	prosyncftp = PsftpConn("192.168.105.181", "prosync", "")
	prosyncftp.getfile("","/home/prosync/" + report.revision + "_log.tgz " + WORKSPACE)
	print WORKSPACE + "/" + report.revision + "_log.tgz"
        #Report Send mail
        qmmail = StmtMail()
	#qmmail.sendmail("dqa2@tmax.co.kr", report.to_whom, ["sangbok_heo@tmax.co.kr", "cheongwoo_lee@tmax.co.kr"] \
	qmmail.sendmail("sangbok_heo@tmax.co.kr", report.to_whom, ["dqa2@tmax.co.kr"] \
                  , "Prosync Test Result and Report " \
                  , html_reports \
                  , WORKSPACE + "/" + report.revision + "_log.tgz")
                  #, None)

    except Exception, e:
        exc_type, exc_value, exc_traceback = sys.exc_info()
        print "[-] Error       : %s " % e
        print "[-] line number :  " , exc_traceback.tb_lineno