<#macro table data tableClass oddRowClass evenRowClass="">
<table class="${tableClass}">
  <thead>
    <tr>
      <#list data[0]?keys as col>
      <th>${col}</th>
      </#list>
    </tr>
  </thead>
  <tbody>
    <#list data as row>
    <tr class="${[oddRowClass, evenRowClass][row_index % 2]}">
      <#list row?keys as col>
      <td>${row[col]}</td>
      </#list>
    </tr>
    </#list>
  </tbody>
</table>
</#macro>

<#macro pure_table data>
<@table data "pure-table pure-table-bordered" "pure-table-odd"/>
</#macro>