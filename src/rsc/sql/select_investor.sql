WITH phs AS (SELECT name AS investmentPhase, investorId AS invId FROM investmentPhaseAssignment INNER JOIN investmentPhase ON 
investmentPhaseAssignment.investmentPhaseId = investmentPhase.id),

invst AS (SELECT id, investorType, username, accountId, investmentMin, investmentMax FROM (investor INNER JOIN 
(SELECT name AS investorType, id AS typeId FROM investorType) AS tp ON investor.investorTypeId = tp.typeId) AS inv INNER JOIN 
(SELECT id as accId, username FROM account) AS acc ON acc.accId = inv.accountId),

spprt AS (SELECT name AS support, accountId AS accId FROM supportAssignment INNER JOIN support ON support.id = supportAssignment.supportId),

cntry AS (SELECT name AS country, accountId AS accId FROM countryAssignment INNER JOIN country ON country.id = countryAssignment.countryId),

cntnt AS (SELECT name AS continent, accountId AS accId FROM continentAssignment INNER JOIN continent ON continent.id = continentAssignment.continentId),

indstr AS (SELECT name AS industry, accountId AS accId FROM industryAssignment INNER JOIN industry ON industry.id = industryAssignment.industryId)

SELECT id, investorType, username, accountId, investmentMin, investmentMax, investmentPhase, support, country, continent, industry
FROM invst LEFT OUTER JOIN phs ON phs.invId = invst.id LEFT OUTER JOIN spprt ON invst.accountId = spprt.accId LEFT OUTER JOIN 
cntry ON cntry.accId = invst.accountId LEFT OUTER JOIN cntnt ON cntnt.accId = invst.accountId
LEFT OUTER JOIN indstr ON indstr.accId = invst.accountId;  