<div class="row project">
    <div class="col-md-8 project-title">
        <p>${project.title}</p>
    </div>
    <div class="col-md-4 project-date">
        <p>${project.pubDate}</p>
    </div>
    <div class="col-md-12">
        <#if project.stopWordMatchesInContent?has_content>
            <div class="stop-word-matches">
                <strong>Stop word matches:</strong>
                <ul>
                    <#list project.stopWordMatchesInContent as stopWordMatch>
                        <li>...${stopWordMatch}...</li>
                    </#list>
                </ul>
            </div>
        </#if>
        <p>${project.description} <a href="${project.link}" target="_blank">More</a></p>
    </div>
</div>