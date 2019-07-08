#!/usr/bin/python
import sys
sys.path.append("/home/autobuild/dbfilegen/common/")
import os
from BeautifulSoup import *
import getopt
import time
import datetime

LOGFILE=time.strftime("log_%Y%m%d%H%M%S")
FILE_GEN_HOME = '/home/autobuild/dbfilegen'

print  datetime.datetime.now()
nowdate = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')

COMMENT =""
option_config = "configM.xml"
option_tac_type =""
option_cm_type=""


def comment_set(template_loc):

    COMMENT ="#################################################################\n" \
           + "#                                 _\\//_                         \n" \
           + "#                                (` * * ')                       \n" \
           + "#______________________________ooO_(_)_Ooo_______________________\n" \
           + "#                                                                \n" \
           + "# @Author           : J.K Min                                    \n" \
           + "# @Gen Date         : " + nowdate + "                            \n" \
           + "# @template locate  : " +template_loc +"                         \n" \
           + "# @Connect          : ???-???-????                               \n" \
           + "#                                                                \n" \
           + "#                            .oooO     Oooo.                     \n" \
           + "#____________________________(   )_____(   )_____________________\n" \
           + "#                             \ (       ) /                      \n" \
           + "#                              \_)     (_/                       \n" \
           + "#################################################################\n" \

    return COMMENT

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

class Conf:
    def __init__(self, soup, filename):
        self.filename = os.path.abspath(filename)
        self.tibero_version                = get_attrs(soup,"tibero_version")
        self.tac_server_home               = get_attrs(soup,"tac_server_home")
        self.tac_server_ip                 = get_attrs(soup,"tac_server_ip")
        self.tac_server_id                 = get_attrs(soup,"tac_server_id")
        self.tac_server_passwd             = get_attrs(soup,"tac_server_passwd")
        self.tac_tb_home                   = get_attrs(soup,"tac_tb_home")
        self.tac_tb_data_home              = get_attrs(soup,"tac_tb_data_home")
        self.tac_log_archive_dest          = get_attrs(soup,"tac_log_archive_dest")
        self.tac_binary_home               = get_attrs(soup,"tac_binary_home")
        self.tac1_listener_port            = get_attrs(soup,"tac1_listener_port")
        self.tac2_listener_port            = get_attrs(soup,"tac2_listener_port")
        self.local_cluster_addr            = get_attrs(soup,"local_cluster_addr")
        self.tac1_local_cluster_port       = get_attrs(soup,"tac1_local_cluster_port")
        self.tac2_local_cluster_port       = get_attrs(soup,"tac2_local_cluster_port")
        self.tac1_cm_port                  = get_attrs(soup,"tac1_cm_port")
        self.tac2_cm_port                  = get_attrs(soup,"tac2_cm_port")
        self.tac1_cm_ui_port               = get_attrs(soup,"tac1_cm_ui_port")
        self.tac2_cm_ui_port               = get_attrs(soup,"tac2_cm_ui_port")        
        self.tac1_cm_resource_file         = get_attrs(soup,"tac1_cm_resource_file")
        self.tac2_cm_resource_file         = get_attrs(soup,"tac2_cm_resource_file")        
        self.tac_event_trace_map           = get_attrs(soup,"tac_event_trace_map")
        self.tsc_user                      = get_attrs(soup,"tsc_user")
        self.tac_include                   = get_attrs(soup,"tac_include")
        self.tsc_user                      = get_attrs(soup,"tsc_user")
        self.tsc_server_home               = get_attrs(soup,"tsc_server_home")
        self.tsc_tb_home                   = get_attrs(soup,"tsc_tb_home")
        self.tsc_tb_data_home              = get_attrs(soup,"tsc_tb_data_home")
        self.tsc_log_archive_dest          = get_attrs(soup,"tsc_log_archive_dest")
        self.tsc_listener_port             = get_attrs(soup,"tsc_listener_port")
        self.tsc_local_cluster_addr        = get_attrs(soup,"tsc_local_cluster_addr")
        self.tsc_local_cluster_port        = get_attrs(soup,"tsc_local_cluster_port")
        self.tsc_cm_port                   = get_attrs(soup,"tsc_cm_port")
        self.tsc_event_trace_map           = get_attrs(soup,"tsc_event_trace_map")
        self.tsc_log_replication_mode      = get_attrs(soup,"tsc_log_replication_mode")
        self.tsc_log_replication_dest_1    = get_attrs(soup,"tsc_log_replication_dest_1")
        self.tsc_standby_file_name_convert = get_attrs(soup,"tsc_standby_file_name_convert")
        self.tas_diskstring                = get_attrs(soup,"tas_diskstring")
        self.tas_listener_port             = get_attrs(soup,"tas_listener_port")
        self.tas_diskspace                 = get_attrs(soup,"tas_diskspace")
        self.tas_diskspace_size            = get_attrs(soup,"tas_diskspace_size")
        self.tas_listener_port1            = get_attrs(soup,"tas_listener_port1")
        self.tas_listener_port2            = get_attrs(soup,"tas_listener_port2")
        self.tas_local_cluster_port1       = get_attrs(soup,"tas_local_cluster_port1")
        self.tas_local_cluster_port2       = get_attrs(soup,"tas_local_cluster_port2")                 
        self.pb_ip                         = get_attrs(soup,"pb_ip")
        self.pb_id                         = get_attrs(soup,"pb_id")
        self.pb_passwd                     = get_attrs(soup,"pb_passwd")
        self.pb_pbhome                     = get_attrs(soup,"pb_pbhome")
        self.pb_tb_home                    = get_attrs(soup,"pb_tb_home")
        self.pb_exec_dir                   = get_attrs(soup,"pb_exec_dir")
        self.pb_profile                    = get_attrs(soup,"pb_profile")
        self.pb_run_sh                     = get_attrs(soup,"pb_run_sh")
        self.pb_session_kill_dir           = get_attrs(soup,"pb_session_kill_dir")
        self.pb_session_kill_run_sh        = get_attrs(soup,"pb_session_kill_run_sh"  )
        self.binary_ip                     = get_attrs(soup,"binary_ip")
        self.binary_id                     = get_attrs(soup,"binary_id")
        self.binary_passwd                 = get_attrs(soup,"binary_passwd")
        self.binary_dir                    = get_attrs(soup,"binary_dir")
        self.binary_name                   = get_attrs(soup,"binary_name")
        self.probench_case                 = get_attrs(soup,"probench_case")
        self.process_kill_case             = get_attrs(soup,"process_kill_case")
        self.session_kill_case             = get_attrs(soup,"session_kill_case")
        self.tac_type                      = option_tac_type
        self.cm_type                       = option_cm_type


    def show(self):
        print "tibero_version               : %s" % self.tibero_version
        print "tac_server_home              : %s" % self.tac_server_home
        print "tac_server_ip                : %s" % self.tac_server_ip
        print "tac_server_id                : %s" % self.tac_server_id
        print "tac_server_passwd            : %s" % self.tac_server_passwd
        print "tac_tb_home                  : %s" % self.tac_tb_home
        print "tac_tb_data_home             : %s" % self.tac_tb_data_home
        print "tac_log_archive_dest         : %s" % self.tac_log_archive_dest
        print "tac_binary_home              : %s" % self.tac_binary_home
        print "tac1_listener_port           : %s" % self.tac1_listener_port
        print "tac2_listener_port           : %s" % self.tac2_listener_port
        print "local_cluster_addr           : %s" % self.local_cluster_addr
        print "tac1_local_cluster_port      : %s" % self.tac1_local_cluster_port
        print "tac2_local_cluster_port      : %s" % self.tac2_local_cluster_port
        print "tac1_cm_port                 : %s" % self.tac1_cm_port
        print "tac2_cm_port                 : %s" % self.tac2_cm_port
        print "tac1_cm_ui_port              : %s" % self.tac1_cm_ui_port
        print "tac2_cm_ui_port              : %s" % self.tac2_cm_ui_port        
        print "tac1_cm_resource_file        : %s" % self.tac1_cm_resource_file
        print "tac2_cm_resource_file        : %s" % self.tac2_cm_resource_file        
        print "tac_event_trace_map          : %s" % self.tac_event_trace_map
        print "tsc_user                     : %s" % self.tsc_user
        print "tac_include                  : %s" % self.tac_include
        print "tsc_user                     : %s" % self.tsc_user
        print "tsc_server_home              : %s" % self.tsc_server_home
        print "tsc_tb_home                  : %s" % self.tsc_tb_home
        print "tsc_tb_data_home             : %s" % self.tsc_tb_data_home
        print "tsc_log_archive_dest         : %s" % self.tsc_log_archive_dest
        print "tsc_listener_port            : %s" % self.tsc_listener_port
        print "tsc_local_cluster_addr       : %s" % self.tsc_local_cluster_addr
        print "tsc_local_cluster_port       : %s" % self.tsc_local_cluster_port
        print "tsc_cm_port                  : %s" % self.tsc_cm_port
        print "tsc_event_trace_map          : %s" % self.tsc_event_trace_map
        print "tsc_log_replication_mode     : %s" % self.tsc_log_replication_mode
        print "tsc_log_replication_dest_1   : %s" % self.tsc_log_replication_dest_1
        print "tsc_standby_file_name_convert: %s" % self.tsc_standby_file_name_convert
        print "tas_diskstring               : %s" % self.tas_diskstring
        print "tas_listener_port1           : %s" % self.tas_listener_port1
        print "tas_listener_port2           : %s" % self.tas_listener_port2  
        print "tas_diskspace                : %s" % self.tas_diskspace
        print "tas_diskspace_size           : %s" % self.tas_diskspace_size
        print "pb_ip                        : %s" % self.pb_ip
        print "pb_id                        : %s" % self.pb_id
        print "pb_passwd                    : %s" % self.pb_passwd
        print "pb_pbhome                    : %s" % self.pb_pbhome
        print "pb_exec_dir                  : %s" % self.pb_exec_dir
        print "pb_profile                   : %s" % self.pb_profile
        print "pb_run_sh                    : %s" % self.pb_run_sh
        print "pb_session_kill_dir          : %s" % self.pb_session_kill_dir
        print "pb_session_kill_run_sh       : %s" % self.pb_session_kill_run_sh
        print "binary_ip                    : %s" % self.binary_ip
        print "binary_id                    : %s" % self.binary_id
        print "binary_passwd                : %s" % self.binary_passwd
        print "binary_dir                   : %s" % self.binary_dir
        print "binary_name                  : %s" % self.binary_name
        print "probench_case                : %s" % self.probench_case     
        print "process_kill_case            : %s" % self.process_kill_case
        print "session_kill_case            : %s" % self.session_kill_case
        print "tac_type                     : %s" % self.tac_type
        print "cm_type                      : %s" % self.cm_type

    def conn_func(self):
        s = "char *username = \"%s\";\n" % self.user
        s += "char *passwd = \"%s\";\n" % self.passwd
        s += "char *alias = \"%s\";" % self.sid
        return s


def makeCMParam(config):
    parameters1="########### " + config.cm_type.upper() +" #######################"+"\n"
    parameters2="########### " + config.cm_type.upper() +" #######################"+"\n"

    parameters1 = parameters1 + "CM_PORT="+config.tac1_cm_ui_port +"\n" \
                + "_CM_LOCAL_ADDR="+config.local_cluster_addr +"\n" \
                + "LOCAL_CLUSTER_ADDR="+config.local_cluster_addr +"\n" \
                + "LOCAL_CLUSTER_PORT="+config.tac1_local_cluster_port +"\n" \


    parameters2 = parameters2 + "CM_PORT="+config.tac2_cm_ui_port +"\n" \
                + "_CM_LOCAL_ADDR="+config.local_cluster_addr +"\n" \
                + "LOCAL_CLUSTER_ADDR="+config.local_cluster_addr +"\n" \
                + "LOCAL_CLUSTER_PORT="+config.tac2_local_cluster_port +"\n" \

    if config.cm_type.upper() == 'OLD_CM' :
        parameters1 = parameters1 \
                    + "CM_CLUSTER_MODE=ACTIVE_SHARED" +"\n" \
                    + "CM_FILE_NAME="+config.tac_tb_data_home+"/cmfile" +"\n" \
                    + "CM_HEARTBEAT_EXPIRE=400" +"\n" \
                    + "CM_WATCHDOG_EXPIRE=380" +"\n" \

        parameters2 = parameters2 \
                    + "CM_CLUSTER_MODE=ACTIVE_SHARED" +"\n" \
                    + "CM_FILE_NAME="+config.tac_tb_data_home+"/cmfile" +"\n" \
                    + "CM_HEARTBEAT_EXPIRE=400" +"\n" \
                    + "CM_WATCHDOG_EXPIRE=380" +"\n" \

    elif config.cm_type.upper() == 'NEW_CM' :
        print " NEW CM Parameter add "


    else:
        raise  "CM TYPE incorrect ........  \n CM_TYPE :1) OLD_CM, NEW_CM  	"

    parameters1 = parameters1 \
                + "###########################################" +"\n\n"
                
    parameters2 = parameters2 \
                + "###########################################" +"\n\n"

    if config.tac_type.upper() == 'TAS' :
        print " NEW CM Parameter add "
        parameters1= parameters1 + "############# " + config.tac_type.upper() +" #######################"+"\n\n"
        parameters2= parameters2 + "############# " + config.tac_type.upper() +" #######################"+"\n\n"
        
        parameters1 = parameters1 + "USE_ACTIVE_STORAGE=Y" +"\n" \
                    + "AS_PORT="+config.tas_listener_port1 +"\n" \

        parameters2 = parameters2 + "USE_ACTIVE_STORAGE=Y" +"\n" \
                    + "AS_PORT="+config.tas_listener_port2 +"\n" \

        parameters1 = parameters1 \
                    + "###########################################"

        parameters2 = parameters2 \
                    + "###########################################"
    elif config.tac_type.upper() == 'TAC' :
        print " TAC Parameter add "
        
    else:
        raise  "Server type incorrect ........  \n CM_TYPE :1) tac , 2) tas  	"


    return (parameters1,parameters2)

def makefile(config,template_path,gen_path):

    print "======== Server Template File List ======================="
    #print "template path : %s  , gen path %s" % (template_path , gen_path)
    for (path, dir, files) in os.walk(template_path):
        for filename in files:
            ext = os.path.splitext(filename)[-1]
            template_file = "%s/%s" % (template_path,filename)
            #print "extfile :%s orgfile :%s" % (ext,os.path.splitext(filename))
            if (ext == '.template') | (ext == '.sh') :
                #print template_file
                # replace config  file
                config_file = open(template_file).read()
                config_file = config_file.decode("euckr")
                config_file = re.sub("<<tibero_version>>"               , config.tibero_version                , config_file)
                config_file = re.sub("<<tac_server_ip>>"                , config.tac_server_ip                 , config_file)
                config_file = re.sub("<<tac_server_id>>"                , config.tac_server_id                 , config_file)
                config_file = re.sub("<<tac_server_passwd>>"            , config.tac_server_passwd             , config_file)
                config_file = re.sub("<<tac_server_home>>"              , config.tac_server_home               , config_file)
                config_file = re.sub("<<tac_tb_home>>"                  , config.tac_tb_home                   , config_file)
                config_file = re.sub("<<tac_tb_data_home>>"             , config.tac_tb_data_home              , config_file)
                config_file = re.sub("<<tac_log_archive_dest>>"         , config.tac_log_archive_dest          , config_file)
                config_file = re.sub("<<tac_binary_home>>"              , config.tac_binary_home               , config_file)
                config_file = re.sub("<<tac1_listener_port>>"           , config.tac1_listener_port            , config_file)
                config_file = re.sub("<<tac2_listener_port>>"           , config.tac2_listener_port            , config_file)
                config_file = re.sub("<<local_cluster_addr>>"           , config.local_cluster_addr            , config_file)
                config_file = re.sub("<<tac1_local_cluster_port>>"      , config.tac1_local_cluster_port       , config_file)
                config_file = re.sub("<<tac2_local_cluster_port>>"      , config.tac2_local_cluster_port       , config_file)
                config_file = re.sub("<<tac1_cm_port>>"                 , config.tac1_cm_port                  , config_file)
                config_file = re.sub("<<tac2_cm_port>>"                 , config.tac2_cm_port                  , config_file)   
                config_file = re.sub("<<tac1_cm_ui_port>>"              , config.tac1_cm_ui_port               , config_file)
                config_file = re.sub("<<tac2_cm_ui_port>>"              , config.tac2_cm_ui_port               , config_file)                
                config_file = re.sub("<<tac1_cm_resource_file>>"        , config.tac1_cm_resource_file         , config_file)
                config_file = re.sub("<<tac2_cm_resource_file>>"        , config.tac2_cm_resource_file         , config_file)                
                config_file = re.sub("<<tac_event_trace_map>>"          , config.tac_event_trace_map           , config_file)
                config_file = re.sub("<<tac_include>>"                  , config.tac_include                   , config_file)
                config_file = re.sub("<<tsc_user>>"                     , config.tsc_user                      , config_file)
                config_file = re.sub("<<tsc_server_home>>"              , config.tsc_server_home               , config_file)
                config_file = re.sub("<<tsc_tb_home>>"                  , config.tsc_tb_home                   , config_file)
                config_file = re.sub("<<tsc_tb_data_home>>"             , config.tsc_tb_data_home              , config_file)
                config_file = re.sub("<<tsc_log_archive_dest>>"         , config.tsc_log_archive_dest          , config_file)
                config_file = re.sub("<<tsc_listener_port>>"            , config.tsc_listener_port             , config_file)
                config_file = re.sub("<<tsc_local_cluster_addr>>"       , config.tsc_local_cluster_addr        , config_file)
                config_file = re.sub("<<tsc_local_cluster_port>>"       , config.tsc_local_cluster_port        , config_file)
                config_file = re.sub("<<tsc_cm_port>>"                  , config.tsc_cm_port                   , config_file)
                config_file = re.sub("<<tsc_event_trace_map>>"          , config.tsc_event_trace_map           , config_file)
                config_file = re.sub("<<tsc_log_replication_mode>>"     , config.tsc_log_replication_mode      , config_file)
                config_file = re.sub("<<tsc_log_replication_dest_1>>"   , config.tsc_log_replication_dest_1    , config_file)
                config_file = re.sub("<<tsc_standby_file_name_convert>>", config.tsc_standby_file_name_convert , config_file)
                config_file = re.sub("<<tas_diskstring>>"               , config.tas_diskstring                , config_file)
                config_file = re.sub("<<tas_diskspace>>"                , config.tas_diskspace                 , config_file)
                config_file = re.sub("<<tas_diskspace_size>>"           , config.tas_diskspace_size            , config_file)                
                config_file = re.sub("<<tas_listener_port1>>"           , config.tas_listener_port1            , config_file)
                config_file = re.sub("<<tas_listener_port2>>"           , config.tas_listener_port2            , config_file)
                config_file = re.sub("<<tas_local_cluster_port1>>"      , config.tas_local_cluster_port1       , config_file)
                config_file = re.sub("<<tas_local_cluster_port2>>"      , config.tas_local_cluster_port2       , config_file)                
                config_file = re.sub("<<pb_ip>>"                        , config.pb_ip                         , config_file)
                config_file = re.sub("<<pb_id>>"                        , config.pb_id                         , config_file)
                config_file = re.sub("<<pb_passwd>>"                    , config.pb_passwd                     , config_file)
                config_file = re.sub("<<pb_pbhome>>"                    , config.pb_pbhome                     , config_file)
                config_file = re.sub("<<pb_tb_home>>"                   , config.pb_tb_home                    , config_file)
                config_file = re.sub("<<pb_exec_dir>>"                  , config.pb_exec_dir                   , config_file)
                config_file = re.sub("<<pb_profile>>"                   , config.pb_profile                    , config_file)
                config_file = re.sub("<<pb_run_sh>>"                    , config.pb_run_sh                     , config_file)
                config_file = re.sub("<<pb_session_kill_dir>>"          , config.pb_session_kill_dir           , config_file)
                config_file = re.sub("<<pb_session_kill_run_sh>>"       , config.pb_session_kill_run_sh        , config_file)
                config_file = re.sub("<<binary_ip>>"                    , config.binary_ip                     , config_file)
                config_file = re.sub("<<binary_id>>"                    , config.binary_id                     , config_file)
                config_file = re.sub("<<binary_passwd>>"                , config.binary_passwd                 , config_file)
                config_file = re.sub("<<binary_dir>>"                   , config.binary_dir                    , config_file)
                config_file = re.sub("<<binary_name>>"                  , config.binary_name                   , config_file)
                config_file = re.sub("<<probench_case>>"                , config.probench_case                 , config_file)
                config_file = re.sub("<<process_kill_case>>"            , config.process_kill_case             , config_file)
                config_file = re.sub("<<session_kill_case>>"            , config.session_kill_case             , config_file)

                #server install type set
                #==== tac install Type ====
                #ex ) 1: tac
                #     2: tas  + tac
                #     3: tac  + tsc
                #==========================
                config_file = re.sub("<<tac_type>>" , config.tac_type , config_file)

                #server Cm type set
                #==== CM Type ====
                #ex ) 1: OLD_CM
                #     2: NEW_CM
                #==========================
                config_file = re.sub("<<cm_type>>" , config.cm_type , config_file)

                #TAC TIP  file Parameter Append

                print "create file :%s" % os.path.splitext(filename)[-2]

                #Comment setting
                COMMENT = comment_set("%s/%s" % (template_path,filename))
                #shell file move parent dir
                if (os.path.splitext(filename)[-2]).endswith('.sh'):
                    f = open("%s/%s" % (os.path.abspath(os.path.join(gen_path, os.pardir)),filename.replace(".template","")), "w")
                    f.write(COMMENT)
                    f.close()
                    f = open("%s/%s" % (os.path.abspath(os.path.join(gen_path, os.pardir)),filename.replace(".template","")), "a").write(config_file.encode('euckr'))


                else:
                    f = open("%s/%s" % (gen_path,filename.replace(".template","")), "w")
                    if (os.path.splitext(filename)[-2]).endswith('.sql') != True:
                        f.write(COMMENT)
                        f.close()

                    f = open("%s/%s" % (gen_path,filename.replace(".template","")), "a").write(config_file.encode('euckr'))

                    add_paramfile=""
                    if config.tibero_version=="tibero5" :
                        add_paramfile = "addparam5.tip"
                    elif config.tibero_version=="tibero5sp1" :
                         add_paramfile = "addparam5sp1.tip"
                    elif config.tibero_version=="tibero6" :
                         add_paramfile = "addparam6.tip"

                    if os.path.splitext(filename)[0] == "tac1.tip" :
                        print "add_paramfile: " + add_paramfile
                        #print "ADD PARAMETER:" + "%s/%s" % (template_path,"addparam.tip")
                        f = open("%s/%s" % (gen_path,"tac1.tip"), "a").write(makeCMParam(config)[0])
                        f = open("%s/%s" % (gen_path,"tac1.tip"),"a").writelines(open("%s/%s" % (template_path,add_paramfile),"r").readlines())

                    if os.path.splitext(filename)[0] == "tac2.tip":
                        print "add_paramfile: " + add_paramfile
                        f = open("%s/%s" % (gen_path,"tac2.tip"), "a").write(makeCMParam(config)[1])
                        f = open("%s/%s" % (gen_path,"tac2.tip"),"a").writelines(open("%s/%s" % (template_path,add_paramfile),"r").readlines())


    print "======== Server Template File List End ===================="

def usage():
    print "daily Test [options] config file   "
    print "-h : help"
    print "-C : config file: configM.xml"


if __name__ == '__main__':
    try:
        opts, args = getopt.getopt(sys.argv[1:],
                                   "hc:s:vrpPO:Ri:dq:C:tl:xXT:VI:kQg:m:fDw:")
    except :
        usage()
        sys.exit(2)


    print "Make file Start"
    for o, a in opts:
        if o == "-h":
            print "======= options ========"
            usage()
            sys.exit(0)
        elif o == "-C":
            option_config = a
            print option_config
        elif o == "-T":
            option_tac_type = a
            print option_tac_type
        elif o == "-c":
            option_cm_type = a
            print option_cm_type


    conf_file =  "%s/config/%s" % (FILE_GEN_HOME,option_config)

    template_path = "%s/%s" % (FILE_GEN_HOME,"template")

    tac_conf_path = os.path.basename(option_config).replace(".xml","").replace("configM","JOB")
    if not os.path.isdir('%s/%s' % (FILE_GEN_HOME ,tac_conf_path)):
        os.mkdir('%s/%s' % (FILE_GEN_HOME ,tac_conf_path))


    gen_path = "%s/%s/%s" % (FILE_GEN_HOME,tac_conf_path,"tac_conf")

    if not os.path.exists(gen_path):
        os.makedirs(gen_path)

    configm = open(conf_file).read()

    server_conf = BeautifulSoup(configm)

    #config file & install file make
    config = Conf(server_conf,conf_file)
    config.show()

    makefile(config,template_path,gen_path)
    print "using config file : %s " % conf_file
    print "create gen_path   : %s " % gen_path



