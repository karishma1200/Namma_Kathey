import React, { useEffect, useMemo, useState } from "react";

const copy = {
  en: {
    appName: "Namma-Kathey",
    tagline: "Regional Hero Storybook",
    heroTitle: "Stories of courage from every corner of Karnataka.",
    heroText:
      "Tap a district, read a vivid local story, hear it aloud, answer a quick quiz, and collect Heritage Badges for your profile.",
    mapTitle: "District Map",
    storyTitle: "Illustrated Story",
    quizTitle: "Hero Quiz",
    statueTitle: "Statue Finder",
    badgeTitle: "Heritage Badge",
    profileTitle: "My Pride Profile",
    readAloud: "Read Aloud",
    stop: "Stop",
    next: "Next",
    previous: "Previous",
    earnBadge: "Earn Badge",
    badgeSaved: "Badge saved",
    reset: "Reset Profile",
    quizCorrect: "Correct. You earned one heritage star.",
    quizWrong: "Try again. The story has the clue.",
    nearest: "Nearest memorial",
    district: "District",
    km: "km away",
    progress: "badges collected",
    emptyProfile: "Choose a district and finish the quiz to begin.",
  },
  kn: {
    appName: "ನಮ್ಮ-ಕಥೆ",
    tagline: "ಪ್ರಾದೇಶಿಕ ವೀರರ ಕಥಾಪುಸ್ತಕ",
    heroTitle: "ಕರ್ನಾಟಕದ ಮೂಲೆಮೂಲೆಗಳಿಂದ ಧೈರ್ಯದ ಕಥೆಗಳು.",
    heroText:
      "ಜಿಲ್ಲೆಯನ್ನು ಆರಿಸಿ, ಸ್ಥಳೀಯ ಕಥೆ ಓದಿ, ಧ್ವನಿಯಲ್ಲಿ ಕೇಳಿ, ಕ್ವಿಜ್ ಉತ್ತರಿಸಿ, ನಿಮ್ಮ ಪ್ರೊಫೈಲಿನಲ್ಲಿ ಪರಂಪರೆ ಬ್ಯಾಡ್ಜ್ ಉಳಿಸಿಕೊಳ್ಳಿ.",
    mapTitle: "ಜಿಲ್ಲಾ ನಕ್ಷೆ",
    storyTitle: "ಚಿತ್ರಕಥೆ",
    quizTitle: "ವೀರರ ಪ್ರಶ್ನೋತ್ತರ",
    statueTitle: "ಪ್ರತಿಮೆ ಹುಡುಕು",
    badgeTitle: "ಪರಂಪರೆ ಬ್ಯಾಡ್ಜ್",
    profileTitle: "ನನ್ನ ಹೆಮ್ಮೆಯ ಪ್ರೊಫೈಲ್",
    readAloud: "ಓದಿ ಕೇಳಿಸಿ",
    stop: "ನಿಲ್ಲಿಸಿ",
    next: "ಮುಂದೆ",
    previous: "ಹಿಂದೆ",
    earnBadge: "ಬ್ಯಾಡ್ಜ್ ಪಡೆಯಿರಿ",
    badgeSaved: "ಬ್ಯಾಡ್ಜ್ ಉಳಿಸಲಾಗಿದೆ",
    reset: "ಪ್ರೊಫೈಲ್ ಮರುಹೊಂದಿಸಿ",
    quizCorrect: "ಸರಿಯಾಗಿದೆ. ನೀವು ಒಂದು ಪರಂಪರೆ ನಕ್ಷತ್ರ ಗಳಿಸಿದ್ದೀರಿ.",
    quizWrong: "ಮತ್ತೊಮ್ಮೆ ಪ್ರಯತ್ನಿಸಿ. ಸುಳಿವು ಕಥೆಯಲ್ಲಿದೆ.",
    nearest: "ಹತ್ತಿರದ ಸ್ಮಾರಕ",
    district: "ಜಿಲ್ಲೆ",
    km: "ಕಿ.ಮೀ ದೂರ",
    progress: "ಬ್ಯಾಡ್ಜ್‌ಗಳು ಸಂಗ್ರಹ",
    emptyProfile: "ಆರಂಭಿಸಲು ಜಿಲ್ಲೆಯನ್ನು ಆರಿಸಿ ಮತ್ತು ಕ್ವಿಜ್ ಮುಗಿಸಿ.",
  },
};

const heroes = [
  {
    id: "kittur",
    district: "Belagavi",
    kannadaDistrict: "ಬೆಳಗಾವಿ",
    hero: "Kittur Rani Chennamma",
    kannadaHero: "ಕಿತ್ತೂರು ರಾಣಿ ಚೆನ್ನಮ್ಮ",
    theme: "Bravery",
    color: "#bf3f2f",
    marker: { top: "12%", left: "24%" },
    memorial: "Kittur Fort Memorial",
    distance: 18,
    question: {
      en: "What did Chennamma protect with courage?",
      kn: "ಚೆನ್ನಮ್ಮ ಧೈರ್ಯದಿಂದ ಏನನ್ನು ರಕ್ಷಿಸಿದರು?",
      options: {
        en: ["Her kingdom and people", "A gold mine", "A ship"],
        kn: ["ತಮ್ಮ ರಾಜ್ಯ ಮತ್ತು ಜನರು", "ಚಿನ್ನದ ಗಣಿ", "ಒಂದು ಹಡಗು"],
      },
      answer: 0,
    },
    story: {
      en: [
        "In Kittur, Rani Chennamma listened carefully to her people before she made a decision. Her court knew her as a ruler who mixed kindness with discipline.",
        "When unfair power tried to take Kittur's freedom, she stood firm. She reminded everyone that love for the land also means responsibility toward its people.",
        "Children remember her because bravery is not only fighting. It is speaking the truth, protecting others, and refusing to give up when the path is hard.",
      ],
      kn: [
        "ಕಿತ್ತೂರಿನಲ್ಲಿ ರಾಣಿ ಚೆನ್ನಮ್ಮ ನಿರ್ಧಾರ ಮಾಡುವ ಮೊದಲು ಜನರ ಮಾತುಗಳನ್ನು ಗಮನದಿಂದ ಕೇಳುತ್ತಿದ್ದರು. ಕರುಣೆ ಮತ್ತು ಶಿಸ್ತನ್ನು ಜೊತೆಯಾಗಿ ಇಟ್ಟ ಆಡಳಿತಗಾರ್ತಿ ಎಂದು ಎಲ್ಲರೂ ಅರಿತಿದ್ದರು.",
        "ಅನ್ಯಾಯವಾದ ಅಧಿಕಾರ ಕಿತ್ತೂರಿನ ಸ್ವಾತಂತ್ರ್ಯ ಕಸಿದುಕೊಳ್ಳಲು ಬಂದಾಗ ಅವರು ದೃಢವಾಗಿ ನಿಂತರು. ನೆಲದ ಮೇಲಿನ ಪ್ರೀತಿ ಜನರ ಮೇಲಿನ ಹೊಣೆಗಾರಿಕೆಯೂ ಆಗಿದೆ ಎಂದು ನೆನಪಿಸಿದರು.",
        "ಮಕ್ಕಳು ಅವರನ್ನು ನೆನಪಿಸಿಕೊಳ್ಳುವುದು ಯುದ್ಧಕ್ಕಾಗಿ ಮಾತ್ರವಲ್ಲ. ಸತ್ಯ ಹೇಳುವುದು, ಇತರರನ್ನು ಕಾಪಾಡುವುದು, ಕಠಿಣ ದಾರಿಯಲ್ಲೂ ಕೈಬಿಡದಿರುವುದೇ ಧೈರ್ಯ.",
      ],
    },
  },
  {
    id: "sangolli",
    district: "Belagavi",
    kannadaDistrict: "ಬೆಳಗಾವಿ",
    hero: "Sangolli Rayanna",
    kannadaHero: "ಸಂಗೊಳ್ಳಿ ರಾಯಣ್ಣ",
    theme: "Loyalty",
    color: "#2f6f66",
    marker: { top: "18%", left: "32%" },
    memorial: "Sangolli Rayanna Memorial, Nandagad",
    distance: 34,
    question: {
      en: "Which value shines in Rayanna's story?",
      kn: "ರಾಯಣ್ಣನ ಕಥೆಯಲ್ಲಿ ಯಾವ ಮೌಲ್ಯ ಹೊಳೆಯುತ್ತದೆ?",
      options: {
        en: ["Loyalty", "Laziness", "Greed"],
        kn: ["ನಿಷ್ಠೆ", "ಆಲಸ್ಯ", "ಲೋಭ"],
      },
      answer: 0,
    },
    story: {
      en: [
        "Sangolli Rayanna was known for loyalty that did not bend when danger arrived. He believed freedom belonged to ordinary families as much as to rulers.",
        "He moved through villages, gathering courage from farmers, friends, and young people who wanted justice. His strength came from the trust of his community.",
        "His story teaches that service is not always loud. Sometimes it is a promise kept every day, even when nobody is watching.",
      ],
      kn: [
        "ಸಂಗೊಳ್ಳಿ ರಾಯಣ್ಣ ಅಪಾಯ ಬಂದಾಗಲೂ ಬಗ್ಗದ ನಿಷ್ಠೆಗೆ ಪ್ರಸಿದ್ಧರು. ಸ್ವಾತಂತ್ರ್ಯ ರಾಜರಿಗಷ್ಟೇ ಅಲ್ಲ, ಸಾಮಾನ್ಯ ಕುಟುಂಬಗಳಿಗೂ ಸೇರಿದೆ ಎಂದು ಅವರು ನಂಬಿದರು.",
        "ನ್ಯಾಯ ಬಯಸಿದ ರೈತರು, ಸ್ನೇಹಿತರು, ಯುವಕರ ಧೈರ್ಯವನ್ನು ಕೂಡಿಸಿ ಅವರು ಗ್ರಾಮಗಳಿಂದ ಗ್ರಾಮಗಳಿಗೆ ಹೋದರು. ಅವರ ಶಕ್ತಿ ಸಮುದಾಯದ ನಂಬಿಕೆಯಿಂದ ಬಂದಿತು.",
        "ಸೇವೆ ಯಾವಾಗಲೂ ಗದ್ದಲವಾಗಿರಬೇಕಿಲ್ಲ ಎಂದು ಅವರ ಕಥೆ ಕಲಿಸುತ್ತದೆ. ಕೆಲವೊಮ್ಮೆ ಅದು ಯಾರೂ ನೋಡದಿದ್ದರೂ ಪ್ರತಿದಿನ ಉಳಿಸುವ ಮಾತು.",
      ],
    },
  },
  {
    id: "onake",
    district: "Chitradurga",
    kannadaDistrict: "ಚಿತ್ರದುರ್ಗ",
    hero: "Onake Obavva",
    kannadaHero: "ಒನಕೆ ಓಬವ್ವ",
    theme: "Presence of Mind",
    color: "#b66a24",
    marker: { top: "48%", left: "54%" },
    memorial: "Chitradurga Fort",
    distance: 6,
    question: {
      en: "What helped Obavva protect the fort?",
      kn: "ಓಬವ್ವ ಕೋಟೆಯನ್ನು ರಕ್ಷಿಸಲು ಏನು ಸಹಾಯ ಮಾಡಿತು?",
      options: {
        en: ["Quick thinking", "A magic ring", "A hidden boat"],
        kn: ["ಚುರುಕು ಚಿಂತನೆ", "ಮಾಯಾ ಉಂಗುರ", "ಮರೆಮಾಡಿದ ದೋಣಿ"],
      },
      answer: 0,
    },
    story: {
      en: [
        "At Chitradurga Fort, Obavva noticed danger while doing ordinary household work. She did not wait for someone else to become brave first.",
        "With courage and quick thinking, she protected the narrow passage into the fort. Her alertness gave others time to respond.",
        "Obavva's story tells children that heroes can rise from everyday life. A watchful mind and a brave heart can protect a whole community.",
      ],
      kn: [
        "ಚಿತ್ರದುರ್ಗ ಕೋಟೆಯಲ್ಲಿ ಮನೆಯ ಕೆಲಸ ಮಾಡುತ್ತಿದ್ದಾಗ ಓಬವ್ವ ಅಪಾಯವನ್ನು ಗಮನಿಸಿದರು. ಮೊದಲು ಮತ್ತೊಬ್ಬರು ಧೈರ್ಯ ತೋರಲಿ ಎಂದು ಕಾಯಲಿಲ್ಲ.",
        "ಧೈರ್ಯ ಮತ್ತು ಚುರುಕು ಚಿಂತನೆಯಿಂದ ಕೋಟೆಯ ಸಣ್ಣ ದಾರಿಯನ್ನು ಅವರು ಕಾಪಾಡಿದರು. ಅವರ ಎಚ್ಚರಿಕೆ ಇತರರಿಗೆ ಪ್ರತಿಕ್ರಿಯಿಸಲು ಸಮಯ ಕೊಟ್ಟಿತು.",
        "ಓಬವ್ವನ ಕಥೆ ವೀರರು ದಿನನಿತ್ಯದ ಬದುಕಿನಿಂದಲೇ ಬರಬಹುದು ಎಂದು ಮಕ್ಕಳಿಗೆ ಹೇಳುತ್ತದೆ. ಎಚ್ಚರದ ಮನಸ್ಸು ಮತ್ತು ಧೈರ್ಯದ ಹೃದಯ ಸಮುದಾಯವನ್ನು ಕಾಪಾಡಬಹುದು.",
      ],
    },
  },
  {
    id: "kanakadasa",
    district: "Udupi",
    kannadaDistrict: "ಉಡುಪಿ",
    hero: "Kanakadasa",
    kannadaHero: "ಕನಕದಾಸ",
    theme: "Equality",
    color: "#7a4fb0",
    marker: { top: "50%", left: "20%" },
    memorial: "Kanakana Kindi, Udupi",
    distance: 3,
    question: {
      en: "What did Kanakadasa's poems ask people to remember?",
      kn: "ಕನಕದಾಸರ ಕೀರ್ತನೆಗಳು ಜನರಿಗೆ ಏನು ನೆನಪಿಸಿದವು?",
      options: {
        en: ["Equality and devotion", "Treasure maps", "Palace rules"],
        kn: ["ಸಮಾನತೆ ಮತ್ತು ಭಕ್ತಿ", "ನಿಧಿ ನಕ್ಷೆಗಳು", "ಅರಮನೆ ನಿಯಮಗಳು"],
      },
      answer: 0,
    },
    story: {
      en: [
        "Kanakadasa used simple songs to ask deep questions. His words reached people who may never have entered royal courts or large schools.",
        "He taught that devotion and dignity belong to every person. A poem could become a lamp when society forgot fairness.",
        "His story invites children to use language kindly. A song, a question, or a small act can make the world more equal.",
      ],
      kn: [
        "ಕನಕದಾಸರು ಸರಳ ಕೀರ್ತನೆಗಳಲ್ಲಿ ಆಳವಾದ ಪ್ರಶ್ನೆಗಳನ್ನು ಕೇಳಿದರು. ಅರಮನೆ ಅಥವಾ ದೊಡ್ಡ ಶಾಲೆಗಳಿಗೆ ಹೋಗದ ಜನರಿಗೂ ಅವರ ಮಾತು ತಲುಪಿತು.",
        "ಭಕ್ತಿ ಮತ್ತು ಗೌರವ ಪ್ರತಿಯೊಬ್ಬರಿಗೂ ಸೇರಿವೆ ಎಂದು ಅವರು ಕಲಿಸಿದರು. ಸಮಾಜ ನ್ಯಾಯ ಮರೆತಾಗ ಕವಿತೆ ಒಂದು ದೀಪವಾಗಬಹುದು.",
        "ಅವರ ಕಥೆ ಭಾಷೆಯನ್ನು ಕರುಣೆಯಿಂದ ಬಳಸಲು ಮಕ್ಕಳನ್ನು ಆಹ್ವಾನಿಸುತ್ತದೆ. ಒಂದು ಹಾಡು, ಒಂದು ಪ್ರಶ್ನೆ, ಒಂದು ಸಣ್ಣ ಕಾರ್ಯ ಲೋಕವನ್ನು ಸಮಾನಗೊಳಿಸಬಹುದು.",
      ],
    },
  },
  {
    id: "kuvempu",
    district: "Shivamogga",
    kannadaDistrict: "ಶಿವಮೊಗ್ಗ",
    hero: "Kuvempu",
    kannadaHero: "ಕುವೆಂಪು",
    theme: "Universal Humanism",
    color: "#4d6fb8",
    marker: { top: "38%", left: "35%" },
    memorial: "Kavishaila, Kuppalli",
    distance: 72,
    question: {
      en: "What did Kuvempu encourage through literature?",
      kn: "ಕುವೆಂಪು ಸಾಹಿತ್ಯದ ಮೂಲಕ ಏನು ಪ್ರೇರೇಪಿಸಿದರು?",
      options: {
        en: ["Human dignity", "Carelessness", "Silence"],
        kn: ["ಮಾನವ ಘನತೆ", "ಅಲಕ್ಷ್ಯ", "ಮೌನ"],
      },
      answer: 0,
    },
    story: {
      en: [
        "Kuvempu wrote about forests, people, language, and the dignity of every human being. His imagination made Karnataka feel vast and welcoming.",
        "He believed education should help children think freely and care deeply. Words were not decorations; they were bridges.",
        "His story teaches that love for one's mother tongue can grow together with respect for the whole world.",
      ],
      kn: [
        "ಕುವೆಂಪು ಕಾಡು, ಜನ, ಭಾಷೆ ಮತ್ತು ಪ್ರತಿಯೊಬ್ಬ ಮಾನವನ ಘನತೆ ಬಗ್ಗೆ ಬರೆದರು. ಅವರ ಕಲ್ಪನೆ ಕರ್ನಾಟಕವನ್ನು ವಿಶಾಲವೂ ಆತ್ಮೀಯವೂ ಮಾಡಿತು.",
        "ಶಿಕ್ಷಣ ಮಕ್ಕಳಿಗೆ ಸ್ವತಂತ್ರವಾಗಿ ಯೋಚಿಸಲು ಮತ್ತು ಆಳವಾಗಿ ಕಾಳಜಿ ವಹಿಸಲು ಸಹಾಯ ಮಾಡಬೇಕು ಎಂದು ಅವರು ನಂಬಿದರು. ಪದಗಳು ಅಲಂಕಾರವಲ್ಲ; ಅವು ಸೇತುವೆಗಳು.",
        "ತಾಯಿಭಾಷೆಯ ಮೇಲಿನ ಪ್ರೀತಿ ಜಗತ್ತಿನ ಗೌರವದೊಂದಿಗೆ ಬೆಳೆದುಬರಬಹುದು ಎಂದು ಅವರ ಕಥೆ ಕಲಿಸುತ್ತದೆ.",
      ],
    },
  },
  {
    id: "ambedkar",
    district: "Bengaluru",
    kannadaDistrict: "ಬೆಂಗಳೂರು",
    hero: "B. R. Ambedkar",
    kannadaHero: "ಬಿ. ಆರ್. ಅಂಬೇಡ್ಕರ್",
    theme: "Justice",
    color: "#245c9a",
    marker: { top: "70%", left: "56%" },
    memorial: "Dr. B. R. Ambedkar Bhavan, Bengaluru",
    distance: 9,
    question: {
      en: "Which ideal is central to Ambedkar's work?",
      kn: "ಅಂಬೇಡ್ಕರ್ ಅವರ ಕಾರ್ಯದ ಕೇಂದ್ರ ಆದರ್ಶ ಯಾವುದು?",
      options: {
        en: ["Justice", "Fear", "Waste"],
        kn: ["ನ್ಯಾಯ", "ಭಯ", "ವ್ಯರ್ಥ"],
      },
      answer: 0,
    },
    story: {
      en: [
        "B. R. Ambedkar studied with extraordinary focus because he knew education could open locked doors. He used knowledge to fight unfairness.",
        "His work on rights and the Constitution helped millions imagine a country where dignity belongs to all. He asked people to think, organize, and act.",
        "His story gives children a clear message: learning is powerful when it helps others stand taller.",
      ],
      kn: [
        "ಬಿ. ಆರ್. ಅಂಬೇಡ್ಕರ್ ಶಿಕ್ಷಣ ಮುಚ್ಚಿದ ಬಾಗಿಲುಗಳನ್ನು ತೆರೆಯಬಹುದು ಎಂದು ತಿಳಿದು ಅಪಾರ ಏಕಾಗ್ರತೆಯಿಂದ ಅಧ್ಯಯನ ಮಾಡಿದರು. ಅನ್ಯಾಯದ ವಿರುದ್ಧ ಜ್ಞಾನವನ್ನು ಬಳಸಿದರು.",
        "ಹಕ್ಕುಗಳು ಮತ್ತು ಸಂವಿಧಾನದ ಮೇಲಿನ ಅವರ ಕೆಲಸ ಎಲ್ಲರಿಗೂ ಘನತೆ ಇರುವ ದೇಶವನ್ನು ಕಲ್ಪಿಸಲು ಲಕ್ಷಾಂತರ ಜನರಿಗೆ ನೆರವಾಯಿತು. ಯೋಚಿಸಿ, ಸಂಘಟಿಸಿ, ಕಾರ್ಯನಿರ್ವಹಿಸಿ ಎಂದು ಹೇಳಿದರು.",
        "ಇತರರು ಗರ್ವದಿಂದ ನಿಲ್ಲಲು ಸಹಾಯ ಮಾಡಿದಾಗ ಕಲಿಕೆಯು ಶಕ್ತಿಯುತವಾಗುತ್ತದೆ ಎಂಬ ಸ್ಪಷ್ಟ ಸಂದೇಶವನ್ನು ಅವರ ಕಥೆ ಮಕ್ಕಳಿಗೆ ಕೊಡುತ್ತದೆ.",
      ],
    },
  },
];

const getSavedBadges = () => {
  try {
    return JSON.parse(localStorage.getItem("nammaKatheyBadges") || "[]");
  } catch {
    return [];
  }
};

function App() {
  const [language, setLanguage] = useState("en");
  const [selectedId, setSelectedId] = useState(heroes[0].id);
  const [page, setPage] = useState(0);
  const [quizChoice, setQuizChoice] = useState(null);
  const [badges, setBadges] = useState(getSavedBadges);
  const [isSpeaking, setIsSpeaking] = useState(false);

  const t = copy[language];
  const selectedHero = heroes.find((hero) => hero.id === selectedId) || heroes[0];
  const heroName = language === "en" ? selectedHero.hero : selectedHero.kannadaHero;
  const districtName =
    language === "en" ? selectedHero.district : selectedHero.kannadaDistrict;
  const storyPages = selectedHero.story[language];
  const questionText = selectedHero.question[language];
  const options = selectedHero.question.options[language];
  const badgeSaved = badges.includes(selectedHero.id);
  const quizAnswered = quizChoice !== null;
  const quizCorrect = quizChoice === selectedHero.question.answer;

  const earnedHeroes = useMemo(
    () => heroes.filter((hero) => badges.includes(hero.id)),
    [badges],
  );

  useEffect(() => {
    localStorage.setItem("nammaKatheyBadges", JSON.stringify(badges));
  }, [badges]);

  useEffect(() => {
    setPage(0);
    setQuizChoice(null);
    window.speechSynthesis?.cancel();
    setIsSpeaking(false);
  }, [selectedId, language]);

  const speakStory = () => {
    if (!("speechSynthesis" in window)) {
      return;
    }

    if (isSpeaking) {
      window.speechSynthesis.cancel();
      setIsSpeaking(false);
      return;
    }

    const utterance = new SpeechSynthesisUtterance(
      `${heroName}. ${storyPages.join(" ")}`,
    );
    utterance.lang = language === "kn" ? "kn-IN" : "en-IN";
    utterance.rate = 0.9;
    utterance.onend = () => setIsSpeaking(false);
    window.speechSynthesis.speak(utterance);
    setIsSpeaking(true);
  };

  const saveBadge = () => {
    if (!quizCorrect || badgeSaved) {
      return;
    }

    setBadges((current) => [...current, selectedHero.id]);
  };

  const resetProfile = () => {
    setBadges([]);
    setQuizChoice(null);
  };

  return (
    <main className="app-shell">
      <section className="hero-band">
        <nav className="topbar" aria-label="App controls">
          <div>
            <p>{t.tagline}</p>
            <strong>{t.appName}</strong>
          </div>
          <div className="language-toggle" aria-label="Language">
            <button
              className={language === "en" ? "active" : ""}
              type="button"
              onClick={() => setLanguage("en")}
            >
              EN
            </button>
            <button
              className={language === "kn" ? "active" : ""}
              type="button"
              onClick={() => setLanguage("kn")}
            >
              ಕನ್ನಡ
            </button>
          </div>
        </nav>

        <div className="hero-layout">
          <div className="hero-copy">
            <span className="eyebrow">{t.appName}</span>
            <h1>{t.heroTitle}</h1>
            <p>{t.heroText}</p>
          </div>

          <div className="hero-portrait" aria-label={heroName}>
            <div className="sun-disc" />
            <div className="portrait-card" style={{ "--accent": selectedHero.color }}>
              <span>{selectedHero.theme}</span>
              <h2>{heroName}</h2>
              <p>{districtName}</p>
            </div>
          </div>
        </div>
      </section>

      <section className="workspace">
        <aside className="district-panel" aria-label={t.mapTitle}>
          <div className="panel-heading">
            <p className="eyebrow">{t.mapTitle}</p>
            <h2>{districtName}</h2>
          </div>

          <div className="karnataka-map">
            {heroes.map((hero) => (
              <button
                key={hero.id}
                className={`map-pin ${hero.id === selectedId ? "selected" : ""}`}
                style={{
                  top: hero.marker.top,
                  left: hero.marker.left,
                  "--accent": hero.color,
                }}
                type="button"
                aria-label={language === "en" ? hero.hero : hero.kannadaHero}
                onClick={() => setSelectedId(hero.id)}
                title={language === "en" ? hero.hero : hero.kannadaHero}
              />
            ))}
          </div>

          <div className="district-list">
            {heroes.map((hero) => (
              <button
                key={hero.id}
                className={hero.id === selectedId ? "active" : ""}
                type="button"
                onClick={() => setSelectedId(hero.id)}
              >
                <span>{language === "en" ? hero.district : hero.kannadaDistrict}</span>
                <strong>{language === "en" ? hero.hero : hero.kannadaHero}</strong>
              </button>
            ))}
          </div>
        </aside>

        <section className="story-panel">
          <div className="story-art" style={{ "--accent": selectedHero.color }}>
            <div className="art-sky" />
            <div className="art-hill" />
            <div className="art-fort" />
            <div className="art-person" />
          </div>

          <div className="story-content">
            <div className="panel-heading">
              <p className="eyebrow">{t.storyTitle}</p>
              <h2>{heroName}</h2>
            </div>
            <p className="story-page">{storyPages[page]}</p>
            <div className="story-controls">
              <button
                type="button"
                onClick={() => setPage((current) => Math.max(current - 1, 0))}
                disabled={page === 0}
              >
                {t.previous}
              </button>
              <span>
                {page + 1} / {storyPages.length}
              </span>
              <button
                type="button"
                onClick={() =>
                  setPage((current) => Math.min(current + 1, storyPages.length - 1))
                }
                disabled={page === storyPages.length - 1}
              >
                {t.next}
              </button>
              <button className="voice-button" type="button" onClick={speakStory}>
                {isSpeaking ? t.stop : t.readAloud}
              </button>
            </div>
          </div>
        </section>

        <section className="learning-grid">
          <article className="quiz-panel">
            <div className="panel-heading">
              <p className="eyebrow">{t.quizTitle}</p>
              <h2>{questionText}</h2>
            </div>
            <div className="quiz-options">
              {options.map((option, index) => (
                <button
                  key={option}
                  className={quizChoice === index ? "selected" : ""}
                  type="button"
                  onClick={() => setQuizChoice(index)}
                >
                  {option}
                </button>
              ))}
            </div>
            {quizAnswered ? (
              <p className={quizCorrect ? "quiz-message good" : "quiz-message bad"}>
                {quizCorrect ? t.quizCorrect : t.quizWrong}
              </p>
            ) : null}
            <button
              className="badge-button"
              type="button"
              onClick={saveBadge}
              disabled={!quizCorrect || badgeSaved}
            >
              {badgeSaved ? t.badgeSaved : t.earnBadge}
            </button>
          </article>

          <article className="finder-panel">
            <div className="panel-heading">
              <p className="eyebrow">{t.statueTitle}</p>
              <h2>{t.nearest}</h2>
            </div>
            <div className="route-card">
              <span>{selectedHero.memorial}</span>
              <strong>
                {selectedHero.distance} {t.km}
              </strong>
              <p>
                {t.district}: {districtName}
              </p>
            </div>
          </article>

          <article className="profile-panel">
            <div className="panel-heading">
              <p className="eyebrow">{t.profileTitle}</p>
              <h2>
                {badges.length} / {heroes.length} {t.progress}
              </h2>
            </div>
            <div className="badge-shelf">
              {earnedHeroes.length ? (
                earnedHeroes.map((hero) => (
                  <span key={hero.id} style={{ "--accent": hero.color }}>
                    {language === "en" ? hero.hero : hero.kannadaHero}
                  </span>
                ))
              ) : (
                <p>{t.emptyProfile}</p>
              )}
            </div>
            <button className="reset-button" type="button" onClick={resetProfile}>
              {t.reset}
            </button>
          </article>
        </section>
      </section>
    </main>
  );
}

export default App;
