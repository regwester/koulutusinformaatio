#ki text version poc


### Requirements

It will only work with Koulutusinformaatio "facet search"


### Install

You need Linux, python and virtualenv

1. Install pip (http://www.pip-installer.org/en/latest/installing.html)
2. Install virtualenv (http://www.virtualenv.org/en/latest/virtualenv.html#installation)
3. For python >= 2.7 argparse not required (from requirements.txt)


### Running

Run the following command to launch the generation of html pages

    % make all SOURCE=https://testi.opintopolku.fi

If the argument SOURCE is not defined the script defaults to value "https://opintopolku.fi". The static output files will be generated in directory "out/".

To install the generated files to a desired destination run the following command

	% make install INSTALL_DIR=koulutusinformaatio-app/src/main/webapp/m/

where INSTALL_DIR is the directory the files will be copied to.
    

### How to put into production

Run the commad above, and move the static generated files

    % mv out/ koulutusinformaatio-app/src/main/webapp/m/

Then add these files into git with something like this (not tested):

    % find . -type f |xargs git add 
    % git commit -a -m "Erityiskoulutukset ja valmentavat"
    % git push


### Background

This is probably the weirdest content generation system you have ever encountered. In this system, the basically the directory structure is a "programming language".

Each file name is a statement in this language. Statments are evaluated in sorted order of the file names. You can use BASIC style "line numbers" to order your statements if you wish, they are ignored.

Files ending with "=" are assignment operators, variable name is the part of the file name before the "=". Variable value is the value of the retval variable when the file is evaluated as Python. 
These files typically use "data.py" library, which contains powerful operations for fetching data from RESTful APIs etc.

Files and directories ending with "-" are ignored.

Directories are iterators that loop over lists, so each file in the directory will be evaluated in the context of each. The output will have a directory with the same name.

Files ending with "+" are index files, they are evaluated in the context of the whole list instead of iterating each list elemetn. They support automatic creation of multiple index files
with links.

Rest of the files are handled as template files, and corresponding file is generated in the output directory. The file name may refer to variables, and in this case
the directory iterator will generate one file for each variable name.


### Example

For example, 10_landing_fi= fetches landing page using Wordpress REST API, referring directly to page ID.

A variable "landing_fi" is defined, with JSON representation of the page and metadata.

File 12_index.html+ is a Jinja2 template, which displays among other things, "landing_fi.content". File has + sign so it will only be evaluated with the list context. 
On root directory list context is meaningless.


### Another example

For example, 01_erityis_koulutukset_fi= fetches a list of all special education courses, and then fetches the detailed information for each item in the list.
The resulting items are stored in variable "erityis_koulutukset_fi".

Directory 20_erityis_koulutukset_fi is an iterator, which takes the variable "erityis_koulutukset_fi" value (which is list of items), and generates a file for each file in 
the target output directory by evaluating the corresponding template. File names are generated by using variables that refer to the item data in the file name. Thus {id}.html
will have name with the "id" value of each item.