- [x] @mentions, #refs, [links](), **formatting**, and <del>tags</del> are supported 
- [x] list syntax is required (any unordered or ordered list supported) 
- [x] this is a complete item 
- [ ] this is an incomplete item

* SHA: 17bb13a686306dd0e95428d89311e45342532db0
* User@SHA: mojombo@17bb13a686306dd0e95428d89311e45342532db0
* User/Repository@SHA: mojombo/jekyll@17bb13a686306dd0e95428d89311e45342532db0
* #Num: #1
* User#Num: mojombo#1
* User/Repository#Num: mojombo/jekyll#1

# heading 1 <a name="ab"></a>
## heading 2


* Item
* Item
* Item

- Item
- Item
- Item
- 
Colons can be used to align columns.

| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |

The outer pipes (|) are optional, and you don't need to make the raw Markdown line up prettily. You can also use inline Markdown.

Markdown | Less | Pretty
--- | --- | ---
*Still* | `renders` | **nicely**
1 | 2 | 3


Below is example trace output I see when executing a search for "van de" to go along 
with my previous example code here. Using the below information I could then generate 
a sql query and run it directly against my database, outside of identityIQ. Based on the results, I may find I need to modify my identity select configuration.
 
 
2013-04-23 12:16:55,925 DEBUG http-8080-3 
sailpoint.persistence.hql:2035 - select distinct  
count(distinct identityAlias)  
from sailpoint.object.Identity identityAlias where (((identityAlias.firstname like :param0 and identityAlias.lastname like :param1) or (identityAlias.lastname like :param2 and identityAlias.lastname like :param3) or identityAlias.name like :param4) and identityAlias.workgroup = :param5)
2013-04-23 12:16:55,926 DEBUG http-8080-3 
sailpoint.persistence.hql:2036 - Query parameters: {param0=van%, param1=de%, param2=van%, param3=%de%, param4=van de%, param5=false}
2013-04-23 12:16:56,149 DEBUG http-8080-3 
sailpoint.persistence.hql:2035 - select distinct identityAlias.id, identityAlias.name, identityAlias.firstname, identityAlias.lastname, identityAlias.email, identityAlias.workgroup, identityAlias.displayName from sailpoint.object.Identity identityAlias where (((identityAlias.firstname like :param0 and identityAlias.lastname like :param1) or (identityAlias.lastname like :param2 and 
identityAlias.lastname like :param3) or identityAlias.name like :param4) and identityAlias.workgroup = :param5) order by identityAlias.firstname, 
identityAlias.lastname, identityAlias.name, identityAlias.id
2013-04-23 12:16:56,150 DEBUG http-8080-3 sailpoint.persistence.hql:2036 - Query parameters: {param0=van%, param1=de%, param2=van%, param3=%de%, param4=van de%, param5=false}



[link](#ab) back to heading 1