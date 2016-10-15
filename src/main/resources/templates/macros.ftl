<#macro table resultSet>
  <table class="result-set">
    <thead>
      <tr>
        <#list resultSet[0]?keys as col>
          <th>${col}</th>
        </#list>
      </tr>
    </thead>
    <tbody>
      <#list resultSet as row>
        <tr>
          <#list row?keys as col>
            <td>${row[col]}</td>
          </#list>
        </tr>
      </#list>
    <tbody>
  </table>
</#macro>