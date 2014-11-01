<div id="body">
	<div id="about">
<?php if ( lang( 'parking_site_lang' ) == 'en' ): ?>
        <p>An Open Data project done in collaboration with <a href="http://montrealouvert.net/donnees-ouvertes-questions-frequemment-demandees/?lang=en" title="Open-Data Montréal">Montréal Ouvert</a>.</p>

        <p>ParkCatcher.com is powered by The City of Montréal Open Data <a href="http://donnees.ville.montreal.qc.ca/" title="Portail données ouvertes">Portal</a>, in compliance with its Open Data <a href="http://donnees.ville.montreal.qc.ca/licence-texte-complet" title="Licence d’utilisation des données ouvertes de la Ville de Montréal">license</a>. You can also <a href="http://donnees.ville.montreal.qc.ca/fiche/stationnement-rue/" title="Stationnement sur rue &ndash; panneaux de signalisation">directly</a> access the data sources in <a href="http://depot.ville.montreal.qc.ca/stationnement-rue/zz-json/data.zip" title=".ZIP 16 MB &ndash; Fichiers classés par catégorie de panneau">JSON</a> and <a href="http://donnees.ville.montreal.qc.ca/fiche/stationnement-rue/" title="Stationnement sur rue &ndash; panneaux de signalisation">KMZ</a> formats.</p>

        <p>Website developed by <a href="http://www.mudar.ca/" title="mudar.ca" rel="me">Mudar Noufal</a>. Art Direction and Graphic Design by <a href="http://cargocollective.com/emmadumesnil/" title="cargocollective.com/emmadumesnil">Emma Dumesnil</a>. Mobile versions for Android and iOS are currently under development&hellip;</p>
        <p>Many thanks to everyone at the The City and The Plateau borough who made this project possible.</p>

        <ul>
            <li>The documentation of the <a href="https://github.com/mudar/ParkCatcher" title="GitHub &ndash; ParkCatcher">Open Source API</a> is available on GitHub.</li>
        <ul>
<?php else: ?>
        <p>Projet de données ouvertes réalisé en collaboration avec <a href="http://montrealouvert.net/donnees-ouvertes-questions-frequemment-demandees/?lang=fr" title="Montréal ouvert">Montréal Ouvert</a>.</p>

        <p>CapteurDeStationnement.com est propulsé par le <a href="http://donnees.ville.montreal.qc.ca/" title="Portail données ouvertes">Portail</a> données ouvertes de la Ville de Montréal en conformité avec sa <a href="http://donnees.ville.montreal.qc.ca/licence-texte-complet" title="Licence d’utilisation des données ouvertes de la Ville de Montréal">licence</a> d’utilisation des données ouvertes. Vous pouvez accéder directement aux <a href="http://donnees.ville.montreal.qc.ca/fiche/stationnement-rue/" title="Stationnement sur rue &ndash; panneaux de signalisation">sources des données</a> en formats <a href="http://depot.ville.montreal.qc.ca/stationnement-rue/zz-json/data.zip" title=".ZIP 16 Mo &ndash; Fichiers classés par catégorie de panneau">JSON</a> et <a href="http://donnees.ville.montreal.qc.ca/fiche/stationnement-rue/" title="Stationnement sur rue &ndash; panneaux de signalisation">KMZ</a>.</p>

        <p>Site web réalisé par <a href="http://www.mudar.ca/" title="mudar.ca" rel="me">Mudar Noufal</a>. Direction artistique et design graphique par <a href="http://cargocollective.com/emmadumesnil/" title="cargocollective.com/emmadumesnil">Emma Dumesnil</a>. Des versions mobiles Android et iOS sont en cours de développement&hellip;</p>
        <p>Un grand merci à toutes les personnes de la Ville de Montréal et l’arrondissement du Plateau Mont-Royal qui ont rendu ce projet possible&nbsp;!</p>

        <ul>
            <li>La documentation du <a href="https://github.com/mudar/ParkCatcher" title="GitHub &ndash; ParkCatcher">API ouvert</a> est disponible sur GitHub.</li>
        </ul>
<?php endif ?>

        <div id="logo_mtl_ouvert">
            <a title="Montréal ouvert" href="http://montrealouvert.net/donnees-ouvertes-questions-frequemment-demandees/?lang=<?php echo lang( 'parking_site_lang' ) ?>"><img title="montrealouvert.net" alt="Montréal Ouvert" src="<?php echo base_url() ?>img/logo_montreal_ouvert_<?php echo lang( 'parking_site_lang' ) ?>.png"></a>
        </div>

        <a id="logo_playstore_android" href="https://play.google.com/store/apps/details?id=ca.mudar.parkcatcher" rel="me" title="<?php echo lang( 'parking_playstore_title' ) ?>" ><span class="hidden">title="<?php echo lang( 'parking_playstore_title' ) ?>" </span></a>

	</div>
</div>