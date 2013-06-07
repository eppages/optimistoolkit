====================================================================
DeploymentOptimizer 1.0 README
====================================================================
Component  Name: DeploymentOptimizer
Software Release Number: 1.0
Release Date of the Software: 2012-04-09

====================================================================
LICENSE AND COPYRIGHT 
====================================================================
Copyright (C) 2012 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

====================================================================
INTRODUCTION 
====================================================================
DeploymentOptimizer(DO) is a decision-making component that can be used
to calculate optimal placement solutions for service deployment in 
different scenarios such as Private Cloud, Bursted Cloud, Federated Cloud, 
and Multi Cloud. Two kinds of optimization criteria are supported, i.e., 
RISK, and COST. Users of this component can specify a PlacementRequest which 
contains the optimization objective, available infrastructure providers(IP), 
as well as other properties (e.g., TREC accessment Endpoints), and get 
corresponding optimal placement solutions if there exists.

====================================================================
NEW FEATURES AND FUNCTIONS IN THIS RELEASE 
====================================================================
This release implements these new features and functionality:
RISK/COST Optimization: Optimization goals such as RISK and COST are supported.

====================================================================
BUG FIXES 
====================================================================
None.

====================================================================
KNOWN LIMITATIONS
====================================================================

====================================================================
GETTING STARTED
====================================================================

SOFTWARE DEPENDENCIES
--------------------------------------------------------------------
Dependences:
1.TRECCommonAPI
2.log4j
3.Service-Manifest-Api
4.wsag4j


HARDWARE REQUIREMENTS
--------------------------------------------------------------------
no.


PLATFORM SUPPORTED
--------------------------------------------------------------------
Operating systems (OS) that support JDK 1.6+.


INSTALLATION INSTRUCTIONS
--------------------------------------------------------------------
Installation instructions can be found at 
https://bscw.scai.fraunhofer.de/bscw/bscw.cgi/283640/DeploymentOptimizer_Installation_Guide.doc.


USING THE SOFTWARE
--------------------------------------------------------------------
This component can only be used as a java library.
User guide can be found at 
https://bscw.scai.fraunhofer.de/bscw/bscw.cgi/283640/DeploymentOptimizer_User_Guide.doc.


TESTING THE SOFTWARE
--------------------------------------------------------------------


====================================================================
CONTRIBUTORS 
====================================================================  
Wubin Li (Viali), Petter Svärd  


====================================================================
CONTACT INFORMATION AND WEBSITE
====================================================================
http://www.optimis-project.eu/

We welcome your feedback, suggestions and contributions. Contact us
via email if you have questions, feedback, code submissions, 
and bug reports.

For general inquiries, see http://www.optimis-project.eu/contact


You can submit bug, patches, software contributions, and feature 
requests using Bugzilla.  
Access Bugzilla at: 
http://itforgebugzilla.atosresearch.eu/bugzilla/enter_bug.cgi?product=Optimis 
====================================================================


====================================================================
SOURCE CODE INFORMATION
====================================================================
source code of this component can be accessed via 
http://pandora.atosorigin.es/svn/optimis/branches/OptimisY2/DeploymentOptimizer


====================================================================
DIRECTORY STRUCTURE 
====================================================================
This directory contains the following subdirectories or files:
- pom.xml: maven description of this project.
- cc-build.xml: cruise control system description file.
- LICENCE.txt: licence statement file.
- src: java source code.
- doc: description of this component.
