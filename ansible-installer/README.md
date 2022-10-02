The automated Ansible installer works to simplify the installation of Helical Insight to a few commands.

# Limitations
* Tomcat needs access to the installation directory
* The installer currently support Red Hat Enterprise Linux. Please feel free to contribute more operating systems.

# 1. Prerequisites
To put in place the installers prerequisites, as root, run below commands:
```
yum -y install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm
yum -y install python-pip git
pip install ansible
```

# 3. Download Helical Insigh github repository and configuring settings
Run below commands to down the Helical Insight repository.
```
git clone https://github.com/helicalinsight/helicalinsight
```
Next, edit helicalinsight/ansible-installer/vars/vars.yml with correct values.

# 3. Run the installer
Run the installer as root, or a normal user with sudo access, as follows:

```
cd /path/to/helicalinsight/ansible-installer
ansible-playbook -i hosts install_helical.yml
```
