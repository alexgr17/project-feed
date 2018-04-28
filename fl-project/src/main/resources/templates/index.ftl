<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>${title}</title>
    <link href="webjars/bootstrap/4.0.0-2/css/bootstrap.min.css" rel="stylesheet">
    <link href="style.css" rel="stylesheet">
</head>

<body>
<nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
    <a class="navbar-brand" href="index.html">Project feed</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExampleDefault" aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarsExampleDefault">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item">
                <a class="nav-link" href="/">Fl.ru feed</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/upwork">Upwork feed</a>
            </li>
        </ul>
    </div>
</nav>

<main role="main">
    <div class="jumbotron">
        <div class="container">
            <h2>${title}</h2>
            <#if rssFeeds??>
                <p>
                    <strong>Rss feeds:</strong>
                    <#list rssFeeds as rssFeed>
                        <a href="${rssFeed.url}" target="_blank">${rssFeed.title}</a>;
                    </#list>
                </p>
            </#if>
            <#if keyWords??>
                <p>
                    <strong>Key words:</strong>
                    <#list keyWords as keyWord>
                        ${keyWord};
                    </#list>
                </p>
            </#if>
            <#if stopWords??>
                <p>
                    <strong>Stop words:</strong>
                    <#list stopWords as stopWord>
                        ${stopWord};
                    </#list>
                </p>
            </#if>
        </div>
    </div>
    <div class="container">
        <#if (lastProjectPubDate?? && lastProjectPubDate > 0)>
            <p><a href="/mark-all-projects-as-read?lastProjectPubDate=${lastProjectPubDate?c}">Mark all projects as read</a></p>
        </#if>
        <h2>Projects</h2>
        <#if projects?has_content>
            <div class="projects">
                <#list projects as project>
                    <#if !project.filtered>
                        <div <#if project.stopWordMatchesInContent?has_content>class="opacity"</#if>>
                            <#include "project_row.ftl">
                        </div>
                    </#if>
                </#list>
            </div>
        <#else>
            <p>All projects are already read</p>
        </#if>
    </div>
</main>

<footer class="container">
    <p>&copy; 2018 Alex Gryaznov</p>
</footer>
</body>
</html>