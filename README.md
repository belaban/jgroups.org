
JGroups web site (jgroups.org)
==============================

* To generate the web site, the following steps have to be taken:
  * Define the location of JGroups and JGroupsArticles (module
    JGroupsArticles) in build.properties. Check out from CVS if necessary
  * Make sure to look at all articles (e.g. Memcached, ReplCache,
    TaskDistribution) in JGroupsArticles:
    * Run OpenOffice and generate the PDF and HTML files for each article
  * Run ./build.sh, this will generate the website in ./build.sh
  * Change ./set to use your SourceForge userid
  * Run ./set. This requires website admin permissions
  * Check jgroups.org: make sure all links are correct

Note that URLs which point to the old docroot (e.g. /javagroupsnew/docs/index.html),
will be redirected to /index.html.


  

