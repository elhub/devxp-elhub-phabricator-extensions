<?php

final class TeamCityXmlBuildBuilder {

    private $xml;
    private $root;

    function __construct(){
        $this->xml = new DOMDocument('1.0', 'UTF-8');
        $this->root = $this->xml->createElement('build');
    }

    function addBuildId($buildId){
        $buildIdElement =
            $this->
            xml->
            createElement('buildType');

        $buildIdElement->setAttribute('id', $buildId);

        $this->root->appendChild($buildIdElement);

        return $this;
    }

    function addPhabBuildId($buildId){
        $this->addProperty("phabricator.BUILD_ID", $buildId);
        return $this;
    }

    function addRevisionId($revisionId){
        $this->addProperty("phabricator.REVISION_ID", $revisionId);
        return $this;
    }

    function addRevisionBuild($revisionBuild){
        $this->addProperty("phabricator.REVISION_BUILD", $revisionBuild);

        return $this;
    }

    function addHarbormasterPHID($phid){
        $this->addProperty('phabricator.HARBORMASTER_TARGET_PHID', $phid);
        return $this;
    }

    function addDiffId($diffId){
        $this->addProperty('phabricator.DIFF_ID', $diffId);
        return $this;
    }

    function build(){
        $this->xml->appendChild($this->root);
        return $this->xml->saveXML();
    }

    private function addProperty($name, $value){
        $this->verifyPropertiesExist();

        $property = $this->xml->createElement('property');
        $property->setAttribute('name', $name);
        $property->setAttribute('value', $value);

        $this->
            root->
            getElementsByTagName('properties')->
            item(0)->
            appendChild($property);
    }

    private function verifyPropertiesExist(){
        if($this->root->getElementsByTagName('properties')->length == 0){
            $propertiesElement = $this->xml->createElement('properties');
            $this->root->appendChild($propertiesElement);
        }
    }
}