#!/usr/bin/python
# -*- coding: utf-8 -*-
import pexpect
import tarfile
import sys
import re
import os
import getopt
import traceback
import shutil
from BeautifulSoup import *

PROMPT = ['# ','>>> ','> ','\$ ', '\] ']


def get_attrs(s, key):
    attr = None
    if s.has_key(key):
        attr =  s[key]
    elif s.find(key) and not isinstance(s.find(key).contents[0], Tag):
        attr = s.find(key).contents[0].decode()
    while attr != None:
        m = re.search('`[^`]*`', attr)
        if m is None:
            break
        sub = os.popen(attr[m.start()+1:m.end()-1]).read().strip()
        attr = attr[:m.start()]+sub+attr[m.end():]
    return attr

# file compress : tar.gz
def tar_add(targetfilename,file_path,files):

    print "targetfilename : %s " % targetfilename
    print "file_path : %s  files : %s" % (file_path, files)

    tf = tarfile.open(targetfilename, "w:gz")
    for name in files.split(' '):
        print name
        # »ý¼º ÆÄÀÏ , archive name
        tf.add('%s/%s' % (file_path,name) ,arcname="%s" % (os.path.basename(name)) )
    tf.close()


class PsshConn:

    def __init__(self , ip , id , passwd, timeout):
        self.ip = ip
        self.id = id
        self.passwd = passwd
        self.timeout = timeout

    def show(self):
        print "ip : %s " % self.ip
        print "id : %s  passwd : %s" % (self.id, self.passwd)

    def sshconn(self):
        ssh_newkey = 'Are you sure you want to continue connecting'
        conn = 'ssh %s@%s' % (self.id , self.ip)
        print "conn : " + conn
        child = pexpect.spawn(conn ,timeout=self.timeout)
	ret = child.expect([pexpect.TIMEOUT, ssh_newkey, '[P|p]assword: '])
        #ret = child.expect( [ssh_newkey, 'password:', pexpect.EOF, pexpect.TIMEOUT], 1 )

        if ret == 0:
            print '[-] Error Connecting : 0 '
            return
        if ret == 1:
            child.sendline('yes')
            ret = child.expect([pexpect.TIMEOUT, \
                '[P|p]assword:'])
            if ret == 0:
                print '[-] Error Connecting : 1'
                return
        child.sendline( self.passwd )
        child.expect(PROMPT)
        return child

class PsshConn2:
    def __init__(self , ip , id , timeout):
        self.ip = ip
        self.id = id
        self.timeout = timeout

    def show(self):
        print "ip : %s " % self.ip
        print "id : %s  passwd : %s" % (self.id, self.passwd)

    def sshconn(self):
        conn = 'ssh %s@%s' % (self.id , self.ip)
        print "conn : " + conn
        child = pexpect.spawn(conn ,timeout=self.timeout)
        child.expect(PROMPT)
        return child

class PsftpConn:
    def __init__(self , ip , id , passwd):
        self.ip = ip
        self.id = id
        self.passwd = passwd

    def show(self):
        print "ip : %s " % ip
        print "id : %s  passwd : %s" % (self.id , self.passwd)

    def sftpconn(self):
        psftp = pexpect.spawn('sftp %s@%s' %(self.id , self.ip) ,timeout=3600)
        psftp.logfile = sys.stdout
        try:
	    if len(self.passwd) > 0 :
            	psftp.expect('(?i)password:')
            	x = psftp.sendline(self.passwd)
            	x = psftp.expect(['Permission denied','sftp>'])
            	if x ==0:
                	print 'Permission denied for password:'
                	print self.binary_passwd
                	psftp.kill(0)
            	else:
               		retval = psftp.exitstatus

        except pexpect.EOF:
            print str(psftp)
            print 'SFTP file transfer failed due to premature end of file.'
        except pexpect.TIMEOUT:
            print str(psftp)
            print 'SFTP file transfer failed due to timeout.'

        return psftp

    def sftpclose(self , psftp):
        try:
            x = psftp.isalive()
            x = psftp.close()
            ret = psftp.exitstatus

        except pexpect.EOF:
            print str(psftp)
            print 'SFTP file transfer failed due to premature end of file.'

        except pexpect.TIMEOUT:
            print str(psftp)
            print 'SFTP file transfer failed due to timeout.'


    def putfile(self , path , file):
        conn = self.sftpconn()
        if len(path) > 0 :
            x = conn.sendline('cd ' + path)
        else:
            x = conn.sendline('put ' + file)
            x = conn.expect('sftp>')
        
	self.sftpclose(conn);

    def getfile(self , path , file):
        conn = self.sftpconn()
	print path + file
        if len(path) > 0 :
            x = conn.sendline('cd ' + path)
        else:
	    x = conn.expect('sftp>')
	    x = conn.sendline('get ' + file)
            #x = conn.sendline('cd ')
            #x = conn.expect('sftp>')
            #x = conn.sendline('get ' + file)
            x = conn.expect('sftp>')

        self.sftpclose(conn);


class Binary:
    def __init__(self , soup):
        self.binary_ip = get_attrs(soup,"binary_ip")
        self.binary_id = get_attrs(soup,"binary_id")
        self.binary_passwd = get_attrs(soup,"binary_passwd")
        self.binary_dir = get_attrs(soup,"binary_dir")
        self.binary_name  = get_attrs(soup,"binary_name")
        self.lasted_prosync_binary_name =""
        self.lasted_tibero_binary_name = ""

    def show(self):
        print "ip : %s " % self.binary_ip
        print "id : %s  passwd : %s" % (self.binary_id, self.binary_passwd)
        print "binary_dir  : %s" % self.binary_dir
        print "binary_name : %s" % self.binary_name


    def prosync_lasted_bin(self):

        conn = PsshConn(self.binary_ip , self.binary_id , self.binary_passwd,60).sshconn()
        conn.sendline('cd %s '  % self.binary_dir)
        conn.expect(PROMPT)
        #conn.expect( [pexpect.EOF, pexpect.TIMEOUT], 1 )

        conn.sendline('ls -lrt |grep  '+ self.binary_name + '| tail -2 | grep tar.gz \r')
        #conn.expect( [pexpect.EOF, pexpect.TIMEOUT], 1 )
        conn.expect(PROMPT)

        print "RECOMAND :[ %s ]" % conn.before
        conn.before.replace(" ",'')
        binaryname = re.search('(prosync3-bin-\w+-\d{1,}-opt.tar.gz)', conn.before).group()
        binaryname =  binaryname.replace("\x1b[1;32m\x1b[K",'')
        #¼­¹ö ¸¶´Ù Á» ´Ù¸§... ¸ð¸£°ÚÀ½?????
#        binaryname =  binaryname.replace("[1;32m[K",'')
#        binaryname =  binaryname.replace("[m[K",'')
#        print "binary name : %s " % binaryname
#        binaryname="tibero6-bin-6_rel_US04-linux64-118463-opt.tar.gz"
        self.lasted_prosync_binary_name = binaryname

    def tibero_lasted_bin(self):

        conn = PsshConn(self.binary_ip , self.binary_id , self.binary_passwd,60).sshconn()
        conn.sendline('cd %s '  % self.binary_dir)
        conn.expect(PROMPT)      

        #conn.expect( [pexpect.EOF, pexpect.TIMEOUT], 1 )
        print "self.binary_name:" +self.binary_name
        conn.sendline('ls -lrt | grep  '+ self.binary_name + '| tail -2 | grep tar.gz \r')
        #conn.expect( [pexpect.EOF, pexpect.TIMEOUT], 1 )
        conn.expect(PROMPT)

        print "RECOMAND :<< %s >>" % conn.before
        binaryname = re.search('(tibero.*.tar.gz)', conn.before).group()
        #³ªÁß¿¡ Å×½ºÆ®ÇÏ¸é¼­ ´Ù½Ã È®ÀÎÇÏÀÚ
#        binaryname = re.search('(tibero-\w+-\d{1,}.tar.gz)', conn.before).group()
        binaryname =  binaryname.replace("\x1b[1;32m\x1b[K",'')
        #¼­¹ö ¸¶´Ù Á» ´Ù¸§... ¸ð¸£°ÚÀ½?????
#        binaryname =  binaryname.replace("[1;32m[K",'')
#        binaryname =  binaryname.replace("[m[K",'')
#        print "binary name : %s " % binaryname
#        binaryname="tibero6-bin-6_rel_US04-linux64-118463-opt.tar.gz"
        self.lasted_tibero_binary_name = binaryname


    def getbinary(self,binaryname):
        print "get binary %s " % binaryname
        psftp = PsftpConn(self.binary_ip , self.binary_id , self.binary_passwd)
        conn = psftp.sftpconn()
        x = conn.sendline('cd ' + self.binary_dir)
        x = conn.expect('sftp>')
        x = conn.sendline('get ' + binaryname)
        x = conn.expect('sftp>')

        psftp.sftpclose(conn)

class MavenPom:
    def __init__(self , soup):
        self.tac_server_ip = get_attrs(soup,"tac_server_ip")
        self.tac_server_id = get_attrs(soup,"tac_server_id")
        self.tac_server_passwd = get_attrs(soup,"tac_server_passwd")
        self.pb_ip = get_attrs(soup,"pb_ip")
        self.pb_id = get_attrs(soup,"pb_id")
        self.pb_passwd = get_attrs(soup,"pb_passwd")
        self.pb_ip = get_attrs(soup,"probench_case")
        self.pb_id = get_attrs(soup,"process_kill_case")
        self.pb_passwd = get_attrs(soup,"session_kill_case")

    def show(self):
        print "ip : %s " % self.tac_server_ip
        print "id : %s  passwd : %s" % (self.tac_server_id , self.tac_server_passwd)

    def makePom(self ,template_file,gen_path,binaryname):

        try:
            print " pom_file: %s  \n,pom_file_gen_path: %s \n,binaryname : %s " % (template_file , gen_path , binaryname)

            pom_file = open(template_file).read()
            pom_file = pom_file.decode("euckr")
            pom_file = re.sub("<<tac_server_ip>>" , self.tac_server_ip , pom_file)
            pom_file = re.sub("<<tac_server_id>>" , self.tac_server_id , pom_file)
            pom_file = re.sub("<<tac_server_passwd>>" , self.tac_server_passwd , pom_file)
            pom_file = re.sub("<<pb_ip>>" , self.pb_ip , pom_file)
            pom_file = re.sub("<<pb_id>>" , self.pb_id , pom_file)
            pom_file = re.sub("<<pb_passwd>>" , self.pb_passwd , pom_file)
            pom_file = re.sub("<<binary_name>>" , binaryname , pom_file)
            pom_file = re.sub("<<probench_case>>" , self.probench_case , pom_file)
            pom_file = re.sub("<<process_kill_case>>" , self.process_kill_case , pom_file)
            pom_file = re.sub("<<session_kill_case>>" , self.session_kill_case , pom_file)

            open("%s/%s" % (gen_path,(os.path.basename(template_file)).replace(".template","")), "w").write(pom_file.encode('euckr'))
        except Exception, e:
            print "[-] Error : %s " % e

class Antbuild:
    def __init__(self , soup):
        self.tac_server_ip = get_attrs(soup,"tac_server_ip")
        self.tac_server_id = get_attrs(soup,"tac_server_id")
        self.tac_server_passwd = get_attrs(soup,"tac_server_passwd")
        self.pb_ip = get_attrs(soup,"pb_ip")
        self.pb_id = get_attrs(soup,"pb_id")
        self.pb_passwd = get_attrs(soup,"pb_passwd")
        self.pb_pbhome = get_attrs(soup,"pb_pbhome")
        self.probench_case = get_attrs(soup,"probench_case")
        self.process_kill_case = get_attrs(soup,"process_kill_case")
        self.session_kill_case = get_attrs(soup,"session_kill_case")

    def show(self):
        print "ip : %s " % self.tac_server_ip
        print "id : %s  passwd : %s" % (self.tac_server_id , self.tac_server_passwd)
        print "probench_case : %s  process_kill_case : %s   session_kill_case : %s" % (self.probench_case , self.process_kill_case , self.session_kill_case)

    def makeBuild(self ,template_file,gen_path,binaryname):

        try:
            print " antbuild_file: %s  \n,antbuild_file_gen_path: %s \n,binaryname : %s " % (template_file , gen_path , binaryname)

            antbuild_file = open(template_file).read()
            antbuild_file = antbuild_file.decode("euckr")
            antbuild_file = re.sub("<<tac_server_ip>>" , self.tac_server_ip , antbuild_file)
            antbuild_file = re.sub("<<tac_server_id>>" , self.tac_server_id , antbuild_file)
            antbuild_file = re.sub("<<tac_server_passwd>>" , self.tac_server_passwd , antbuild_file)
            antbuild_file = re.sub("<<pb_ip>>" , self.pb_ip , antbuild_file)
            antbuild_file = re.sub("<<pb_id>>" , self.pb_id , antbuild_file)
            antbuild_file = re.sub("<<pb_passwd>>" , self.pb_passwd , antbuild_file)
            antbuild_file = re.sub("<<pb_pbhome>>" , self.pb_pbhome , antbuild_file)
            antbuild_file = re.sub("<<binary_name>>" , binaryname , antbuild_file)
            antbuild_file = re.sub("<<probench_case>>" , self.probench_case , antbuild_file)
            antbuild_file = re.sub("<<process_kill_case>>" , self.process_kill_case , antbuild_file)
            antbuild_file = re.sub("<<session_kill_case>>" , self.session_kill_case , antbuild_file)

            open("%s/%s" % (gen_path,(os.path.basename(template_file)).replace(".template","")), "w").write(antbuild_file.encode('euckr'))
        except Exception, e:
            print "[-] Error : %s " % e



if __name__=="__main__":


    config = open('/home/autobuild/dbfilegen/prosyncreg/config/prosync_116_100.xml').read()
    prosync_conf = BeautifulSoup(config)
    binary = Binary(prosync_conf)
    binary.show()
    binary = binary.lasted_bin()
    print binary




