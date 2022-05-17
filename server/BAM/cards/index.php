<?php

include_once "../../php/Utils/RequestAPI.php";
include_once "../../php/Utils/Response.php";
include_once "../../php/Database/PDOController.php";
include_once "../../php/Utils/DataStream.php";
include_once "../../php/Utils/SessionUtils.php";


$method = RequestAPI::getMethod();

function getSingleCard($cardId){
    if(!isset($_SESSION['userId'])){
        return Response::message("Unauthorized", 400);
    }
    $matchingCards = PDOController::getCommand("SELECT * FROM bam_cards WHERE cardId = :cardId AND cardOwner = :userId",
        ["cardId"=>$cardId, "userId"=>$_SESSION['userId']]
    );

    if(count($matchingCards)==0 || count($matchingCards) > 1){
        return Response::message("Unauthorized, too many cards", 400);
    }

    return json_encode($matchingCards[0]);
}

function getAllCards(){
    if(!isset($_SESSION['userId'])){
        return Response::message("Unauthorized", 400);
    }
    $matchingCards = PDOController::getCommand("SELECT * FROM bam_cards WHERE cardOwner = :userId",
        ["userId"=>$_SESSION['userId']]
    );

    return json_encode($matchingCards);
}

function addCard() {
    if(!isset($_SESSION['userId'])){
        return Response::message("Unauthorized", 400);
    }
    $data = RequestAPI::getJSON();
    $insertedId = PDOController::insertCommand("
INSERT INTO `bam_cards` (`cardId`, `cardNumber`, `mothYear`, `CVC`, `cardProvider`, `cardName`, `cardOwner`)
VALUES (NULL, :cardNumber, :mothYear, :CVC, :cardProvider , :cardName, :cardOwner);", [
    "cardNumber" => $data['cardNumber'],
    "mothYear" => $data['mothYear'],
    "CVC" => $data['CVC'],
    "cardProvider" => $data['cardProvider'],
    "cardName" => $data['cardName'],
    "cardOwner" => $_SESSION['userId'],
    ]);

    return json_encode(["cardId"=>$insertedId]);
}
function updateCard() {
    if(!isset($_SESSION['userId'])){
        return Response::message("Unauthorized", 400);
    }
    $data = RequestAPI::getJSON();

    PDOController::putCommand("
UPDATE bam_cards 
SET cardNumber = :cardNumber, 
    mothYear = :monthYear, 
    CVC = :CVC, 
    cardProvider = :cardProvider, 
    cardName = :cardName
WHERE 
      cardId = :cardId AND cardOwner = :cardOwner
", [
    "cardNumber" => $data['cardNumber'],
    "monthYear" => $data['monthYear'],
    "CVC" => $data['CVC'],
    "cardProvider" => $data['cardProvider'],
    "cardName" => $data['cardName'],

    "cardId" => $data['cardId'], "cardOwner" => $_SESSION['userId']
    ]);

    return getSingleCard($data['cardId']);
}

function deleteCard(){
    if(!isset($_SESSION['userId'])){
        return Response::message("Unauthorized", 400);
    }
    $data = RequestAPI::getJSON();

    $deletedCards = PDOController::putCommand("DELETE FROM bam_cards WHERE cardOwner = :cardOwner AND cardId = :cardId", [
        "cardId" => $data['cardId'], "cardOwner" => $_SESSION['userId']
    ]);

    return $deletedCards;
}
switch ($method){
    case "GET":
        if(isset($_GET['cardId'])) {
            echo getSingleCard($_GET['cardId']);
        } else {
            echo getAllCards();
        }
        break;
    case "POST":
        echo addCard();
        break;
    case "PUT":
       echo updateCard();
        break;
    case "DELETE":
        echo deleteCard();
        break;
}